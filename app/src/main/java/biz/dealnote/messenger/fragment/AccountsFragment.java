package biz.dealnote.messenger.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import biz.dealnote.messenger.Constants;
import biz.dealnote.messenger.Extra;
import biz.dealnote.messenger.R;
import biz.dealnote.messenger.activity.ActivityUtils;
import biz.dealnote.messenger.activity.LoginActivity;
import biz.dealnote.messenger.activity.ProxyManagerActivity;
import biz.dealnote.messenger.adapter.AccountAdapter;
import biz.dealnote.messenger.api.Auth;
import biz.dealnote.messenger.db.DBHelper;
import biz.dealnote.messenger.dialog.DirectAuthDialog;
import biz.dealnote.messenger.domain.IAccountsInteractor;
import biz.dealnote.messenger.domain.IOwnersRepository;
import biz.dealnote.messenger.domain.InteractorFactory;
import biz.dealnote.messenger.domain.Repository;
import biz.dealnote.messenger.fragment.base.BaseFragment;
import biz.dealnote.messenger.longpoll.LongpollInstance;
import biz.dealnote.messenger.model.Account;
import biz.dealnote.messenger.model.User;
import biz.dealnote.messenger.settings.Settings;
import biz.dealnote.messenger.util.Objects;
import biz.dealnote.messenger.util.PhoenixToast;
import biz.dealnote.messenger.util.RxUtils;
import biz.dealnote.messenger.util.ShortcutUtils;
import biz.dealnote.messenger.util.Utils;
import io.reactivex.disposables.CompositeDisposable;

public class AccountsFragment extends BaseFragment implements View.OnClickListener, AccountAdapter.Callback {

    private TextView empty;
    private RecyclerView mRecyclerView;
    private AccountAdapter mAdapter;
    private ArrayList<Account> mData;

    private IOwnersRepository mOwnersInteractor;
    private IAccountsInteractor accountsInteractor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mOwnersInteractor = Repository.INSTANCE.getOwners();
        accountsInteractor = InteractorFactory.createAccountInteractor();

        if (savedInstanceState != null) {
            mData = savedInstanceState.getParcelableArrayList(SAVE_DATA);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_accounts, container, false);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(root.findViewById(R.id.toolbar));

        empty = root.findViewById(R.id.empty);
        mRecyclerView = root.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false));

        root.findViewById(R.id.fab).setOnClickListener(this);
        root.findViewById(R.id.kate_acc).setOnClickListener(this);
        root.findViewById(R.id.dav_acc).setOnClickListener(this);
        root.findViewById(R.id.dav_acc).setVisibility(View.GONE);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        boolean firstRun = false;
        if (mData == null) {
            mData = new ArrayList<>();
            firstRun = true;
        }

        mAdapter = new AccountAdapter(requireActivity(), mData, this);
        mRecyclerView.setAdapter(mAdapter);

        if (firstRun) {
            load();
        }

        resolveEmptyText();
    }

    private void resolveEmptyText() {
        if (!isAdded() || empty == null) return;
        empty.setVisibility(Utils.safeIsEmpty(mData) ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ActivityUtils.supportToolbarFor(this);
        if (actionBar != null) {
            actionBar.setTitle(null);
            actionBar.setSubtitle(null);
        }
    }

    private static final String SAVE_DATA = "save_data";

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SAVE_DATA, mData);
    }

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    public void onDestroy() {
        mCompositeDisposable.dispose();
        super.onDestroy();
    }

    private void load() {
        mCompositeDisposable.add(accountsInteractor
                .getAll()
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(appAccounts -> {
                    mData.clear();
                    mData.addAll(appAccounts);

                    if (Objects.nonNull(mAdapter)) {
                        mAdapter.notifyDataSetChanged();
                    }

                    resolveEmptyText();
                }));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_LOGIN:
                if (resultCode == Activity.RESULT_OK) {
                    int uid = data.getExtras().getInt(Extra.USER_ID);
                    String token = data.getStringExtra(Extra.TOKEN);
                    String Login = data.getStringExtra(Extra.LOGIN);
                    String Password = data.getStringExtra(Extra.PASSWORD);
                    processNewAccount(uid, token, "vkandroid", Login != null ? Login : "", Password != null ? Password : "", true, true);
                }
                break;

            case REQEUST_DIRECT_LOGIN:
                if (resultCode == Activity.RESULT_OK) {
                    if (DirectAuthDialog.ACTION_LOGIN_VIA_WEB.equals(data.getAction())) {
                        startLoginViaWeb();
                    }
                    else if(DirectAuthDialog.ACTION_VALIDATE_VIA_WEB.equals(data.getAction())) {
                        String url = data.getStringExtra(Extra.URL);
                        String Login = data.getStringExtra(Extra.LOGIN);
                        String Password = data.getStringExtra(Extra.PASSWORD);
                        startValidateViaWeb(url, Login, Password);
                    } else if (DirectAuthDialog.ACTION_LOGIN_COMPLETE.equals(data.getAction())) {
                        int uid = data.getExtras().getInt(Extra.USER_ID);
                        String token = data.getStringExtra(Extra.TOKEN);
                        String Login = data.getStringExtra(Extra.LOGIN);
                        String Password = data.getStringExtra(Extra.PASSWORD);
                        processNewAccount(uid, token, "vkandroid", Login, Password , true, true);
                    }
                }
                break;
        }
    }

    private int indexOf(int uid) {
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getId() == uid) {
                return i;
            }
        }

        return -1;
    }

    private void merge(Account account) {
        int index = indexOf(account.getId());

        if (index != -1) {
            mData.set(index, account);
        } else {
            mData.add(account);
        }

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }

        resolveEmptyText();
    }

    private void processNewAccount(final int uid, final String token, final String type, final String Login, final String Password, boolean IsSend, boolean isCurrent) {
        //Accounts account = new Accounts(token, uid);

        // важно!! Если мы получили новый токен, то необходимо удалить запись
        // о регистрации push-уведомлений
        //PushSettings.unregisterFor(getContext(), account);

        Settings.get()
                .accounts()
                .storeAccessToken(uid, token);

        Settings.get()
                .accounts().storeTokenType(uid, type);

        Settings.get()
                .accounts()
                .registerAccountId(uid, isCurrent);

        merge(new Account(uid, null));

        mCompositeDisposable.add(mOwnersInteractor.getBaseOwnerInfo(uid, uid, IOwnersRepository.MODE_ANY)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(owner -> merge(new Account(uid, owner)), t -> {/*ignored*/}));
    }

    private static final int REQUEST_LOGIN = 107;
    private static final int REQEUST_DIRECT_LOGIN = 108;

    private void startLoginViaWeb() {
        Intent intent = LoginActivity.createIntent(requireActivity(), String.valueOf(Constants.API_ID), Auth.getScope());
        startActivityForResult(intent, REQUEST_LOGIN);
    }

    private void startValidateViaWeb(String url, String Login, String Password) {
        Intent intent = LoginActivity.createIntent(requireActivity(), url, Login, Password);
        startActivityForResult(intent, REQUEST_LOGIN);
    }

    private void startDirectLogin() {
        DirectAuthDialog.newInstance()
                .targetTo(this, REQEUST_DIRECT_LOGIN)
                .show(requireFragmentManager(), "direct-login");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                startDirectLogin();
                break;
            case R.id.kate_acc:
                onKate();
                break;
        }
    }

    private boolean canRunRootCommands()
    {
        boolean retval = false;
        Process suProcess;
        PhoenixToast.showToast(requireActivity(), R.string.get_root);
        try
        {
            suProcess = Runtime.getRuntime().exec("su");

            DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
            DataInputStream osRes = new DataInputStream(suProcess.getInputStream());

            if (null != os && null != osRes)
            {
                os.writeBytes("id\n");
                os.flush();

                String currUid = osRes.readLine();
                boolean exitSu = false;
                if (null == currUid)
                {
                    retval = false;
                    exitSu = false;
                    PhoenixToast.showToastError(requireActivity(), "Can't get root access or denied by user");
                }
                else if (true == currUid.contains("uid=0"))
                {
                    retval = true;
                    exitSu = true;
                }
                else
                {
                    retval = false;
                    exitSu = true;
                    PhoenixToast.showToastError(requireActivity(), "Root access rejected: \" + currUid");
                }
                if (exitSu)
                {
                    os.writeBytes("exit\n");
                    os.flush();
                }
            }
        }
        catch (Exception e)
        {
            retval = false;
            PhoenixToast.showToastError(requireActivity(), "Root access rejected [" + e.getClass().getName() + "] : " + e.getMessage());
        }

        return retval;
    }

    private void onKate()
    {
        if(!canRunRootCommands())
            return;
        String JSDT = new String();
        try {
            Process suProcess;
            suProcess = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
            DataInputStream osRes = new DataInputStream(suProcess.getInputStream());
            if (null != os && null != osRes) {
                os.writeBytes("cat /data/data/com.perm.kate_new_6/shared_prefs/com.perm.kate_new_6_preferences.xml\n");
                os.flush();
                TimeUnit.SECONDS.sleep(1);
                while(osRes.available()>0) {
                    JSDT += osRes.readLine();
                }
                os.writeBytes("exit\n");
                os.flush();
                suProcess.waitFor();
            }
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new StringReader(JSDT)));
            NodeList elements = doc.getElementsByTagName("map").item(0).getChildNodes();
            for (int i = 0; i < elements.getLength(); i++) {
                NamedNodeMap attributes = elements.item(i).getAttributes();
                if(attributes == null || attributes.getNamedItem("name") == null)
                    continue;
                String name = attributes.getNamedItem("name").getNodeValue();
                if(name.equals("accounts"))
                {
                    JSONArray jsonRoot = new JSONArray(elements.item(i).getTextContent());
                    for(int s = 0; s < jsonRoot.length(); s++) {
                        JSONObject mJsonObject = jsonRoot.getJSONObject(s);
                        processNewAccount(mJsonObject.getInt("mid"), mJsonObject.getString("access_token"), "kate", "", "", true, false);
                    }
                    break;
                }

            }
        }
        catch (Exception e)
        {
            PhoenixToast.showToastError(requireActivity(), "Root access rejected [" + e.getClass().getName() + "] : " + e.getMessage());
        }
    }

    private void delete(Account account) {
        Settings.get()
                .accounts()
                .removeAccessToken(account.getId());

        Settings.get()
                .accounts()
                .removeType(account.getId());

        Settings.get()
                .accounts()
                .remove(account.getId());

        DBHelper.removeDatabaseFor(requireActivity(), account.getId());

        LongpollInstance.get().forceDestroy(account.getId());

        mData.remove(account);
        mAdapter.notifyDataSetChanged();
        resolveEmptyText();
    }

    private void setAsActive(Account account) {
        Settings.get()
                .accounts()
                .setCurrent(account.getId());

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(final Account account) {
        boolean idCurrent = account.getId() == Settings.get()
                .accounts()
                .getCurrent();

        String[] items;

        if (account.getId() > 0) {
            if (idCurrent) {
                items = new String[]{getString(R.string.delete), getString(R.string.add_to_home_screen)};
            } else {
                items = new String[]{getString(R.string.delete), getString(R.string.add_to_home_screen), getString(R.string.set_as_active)};
            }
        } else {
            items = new String[]{getString(R.string.delete)};
        }

        new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(account.getDisplayName())
                .setItems(items, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            delete(account);
                            break;
                        case 1:
                            createShortcut(account);
                            break;
                        case 2:
                            setAsActive(account);
                            break;
                    }
                })
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_ok) {
            requireActivity().finish();
            return true;
        }

        if (item.getItemId() == R.id.privacy_policy) {
            showPrivacyPolicy();
            return true;
        }

        if (item.getItemId() == R.id.action_proxy) {
            startProxySettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startProxySettings() {
        startActivity(new Intent(requireActivity(), ProxyManagerActivity.class));
    }

    private void showPrivacyPolicy() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.PRIVACY_POLICY_LINK));
        startActivity(browserIntent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_accounts, menu);
    }

    private void createShortcut(final Account account) {
        if (account.getId() < 0) {
            return; // this is comminity
        }

        User user = (User) account.getOwner();

        final Context app = requireContext().getApplicationContext();
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String avaUrl = user == null ? null : user.getMaxSquareAvatar();
                try {
                    ShortcutUtils.createAccountShurtcut(app, account.getId(), account.getDisplayName(), avaUrl);
                } catch (IOException e) {
                    return e.getMessage();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                if (Utils.nonEmpty(s)) {
                    Toast.makeText(app, s, Toast.LENGTH_LONG).show();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}