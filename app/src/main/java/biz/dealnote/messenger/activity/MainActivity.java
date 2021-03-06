package biz.dealnote.messenger.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.stream.JsonReader;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import biz.dealnote.messenger.Constants;
import biz.dealnote.messenger.Extra;
import biz.dealnote.messenger.Injection;
import biz.dealnote.messenger.R;
import biz.dealnote.messenger.api.HttpLogger;
import biz.dealnote.messenger.api.ProxyUtil;
import biz.dealnote.messenger.db.Stores;
import biz.dealnote.messenger.dialog.ResolveDomainDialog;
import biz.dealnote.messenger.domain.InteractorFactory;
import biz.dealnote.messenger.domain.impl.CountersInteractor;
import biz.dealnote.messenger.fragment.AbsWallFragment;
import biz.dealnote.messenger.fragment.AdditionalNavigationFragment;
import biz.dealnote.messenger.fragment.AnswerVKOfficialFragment;
import biz.dealnote.messenger.fragment.AudioCatalogFragment;
import biz.dealnote.messenger.fragment.AudioPlayerFragment;
import biz.dealnote.messenger.fragment.AudiosFragment;
import biz.dealnote.messenger.fragment.AudiosInCatalogFragment;
import biz.dealnote.messenger.fragment.AudiosTabsFragment;
import biz.dealnote.messenger.fragment.BrowserFragment;
import biz.dealnote.messenger.fragment.ChatFragment;
import biz.dealnote.messenger.fragment.ChatUsersFragment;
import biz.dealnote.messenger.fragment.CommentsFragment;
import biz.dealnote.messenger.fragment.CommunitiesFragment;
import biz.dealnote.messenger.fragment.CommunityBanEditFragment;
import biz.dealnote.messenger.fragment.CommunityControlFragment;
import biz.dealnote.messenger.fragment.CommunityInfoContactsFragment;
import biz.dealnote.messenger.fragment.CommunityInfoLinksFragment;
import biz.dealnote.messenger.fragment.CommunityManagerEditFragment;
import biz.dealnote.messenger.fragment.CreatePhotoAlbumFragment;
import biz.dealnote.messenger.fragment.CreatePollFragment;
import biz.dealnote.messenger.fragment.DialogsFragment;
import biz.dealnote.messenger.fragment.DialogsTabsFragment;
import biz.dealnote.messenger.fragment.DocPreviewFragment;
import biz.dealnote.messenger.fragment.DocsFragment;
import biz.dealnote.messenger.fragment.DrawerEditFragment;
import biz.dealnote.messenger.fragment.FeedFragment;
import biz.dealnote.messenger.fragment.FeedbackFragment;
import biz.dealnote.messenger.fragment.FwdsFragment;
import biz.dealnote.messenger.fragment.GifPagerFragment;
import biz.dealnote.messenger.fragment.ImportantMessagesFragment;
import biz.dealnote.messenger.fragment.LikesFragment;
import biz.dealnote.messenger.fragment.LinksInCatalogFragment;
import biz.dealnote.messenger.fragment.LogsFragement;
import biz.dealnote.messenger.fragment.MessagesLookFragment;
import biz.dealnote.messenger.fragment.NewsfeedCommentsFragment;
import biz.dealnote.messenger.fragment.NewsfeedMentionsFragment;
import biz.dealnote.messenger.fragment.NotificationPreferencesFragment;
import biz.dealnote.messenger.fragment.PhotoPagerFragment;
import biz.dealnote.messenger.fragment.PlaylistsInCatalogFragment;
import biz.dealnote.messenger.fragment.PollFragment;
import biz.dealnote.messenger.fragment.PreferencesFragment;
import biz.dealnote.messenger.fragment.RequestExecuteFragment;
import biz.dealnote.messenger.fragment.SecurityPreferencesFragment;
import biz.dealnote.messenger.fragment.ShortedLinksFragment;
import biz.dealnote.messenger.fragment.SinglePhotoFragment;
import biz.dealnote.messenger.fragment.StoryPagerFragment;
import biz.dealnote.messenger.fragment.ThemeFragment;
import biz.dealnote.messenger.fragment.TopicsFragment;
import biz.dealnote.messenger.fragment.UserBannedFragment;
import biz.dealnote.messenger.fragment.UserDetailsFragment;
import biz.dealnote.messenger.fragment.VKPhotoAlbumsFragment;
import biz.dealnote.messenger.fragment.VKPhotosFragment;
import biz.dealnote.messenger.fragment.VideoPreviewFragment;
import biz.dealnote.messenger.fragment.VideosFragment;
import biz.dealnote.messenger.fragment.VideosInCatalogFragment;
import biz.dealnote.messenger.fragment.VideosTabsFragment;
import biz.dealnote.messenger.fragment.WallPostFragment;
import biz.dealnote.messenger.fragment.attachments.CommentCreateFragment;
import biz.dealnote.messenger.fragment.attachments.CommentEditFragment;
import biz.dealnote.messenger.fragment.attachments.PostCreateFragment;
import biz.dealnote.messenger.fragment.attachments.PostEditFragment;
import biz.dealnote.messenger.fragment.attachments.RepostFragment;
import biz.dealnote.messenger.fragment.conversation.ConversationFragmentFactory;
import biz.dealnote.messenger.fragment.fave.FaveTabsFragment;
import biz.dealnote.messenger.fragment.friends.FriendsTabsFragment;
import biz.dealnote.messenger.fragment.search.SearchTabsFragment;
import biz.dealnote.messenger.fragment.search.SingleTabSearchFragment;
import biz.dealnote.messenger.fragment.wallattachments.WallAttachmentsFragmentFactory;
import biz.dealnote.messenger.link.LinkHelper;
import biz.dealnote.messenger.listener.AppStyleable;
import biz.dealnote.messenger.listener.BackPressCallback;
import biz.dealnote.messenger.listener.OnSectionResumeCallback;
import biz.dealnote.messenger.modalbottomsheetdialogfragment.ModalBottomSheetDialogFragment;
import biz.dealnote.messenger.modalbottomsheetdialogfragment.OptionRequest;
import biz.dealnote.messenger.model.Banned;
import biz.dealnote.messenger.model.Comment;
import biz.dealnote.messenger.model.Document;
import biz.dealnote.messenger.model.Manager;
import biz.dealnote.messenger.model.Peer;
import biz.dealnote.messenger.model.SectionCounters;
import biz.dealnote.messenger.model.User;
import biz.dealnote.messenger.model.UserDetails;
import biz.dealnote.messenger.model.drawer.AbsMenuItem;
import biz.dealnote.messenger.model.drawer.RecentChat;
import biz.dealnote.messenger.model.drawer.SectionMenuItem;
import biz.dealnote.messenger.mvp.presenter.DocsListPresenter;
import biz.dealnote.messenger.mvp.view.IVideosListView;
import biz.dealnote.messenger.mvp.view.IVkPhotosView;
import biz.dealnote.messenger.place.Place;
import biz.dealnote.messenger.place.PlaceFactory;
import biz.dealnote.messenger.place.PlaceProvider;
import biz.dealnote.messenger.player.MusicPlaybackService;
import biz.dealnote.messenger.player.util.MusicUtils;
import biz.dealnote.messenger.push.IPushRegistrationResolver;
import biz.dealnote.messenger.settings.CurrentTheme;
import biz.dealnote.messenger.settings.ISettings;
import biz.dealnote.messenger.settings.Settings;
import biz.dealnote.messenger.settings.SwipesChatMode;
import biz.dealnote.messenger.util.Accounts;
import biz.dealnote.messenger.util.Action;
import biz.dealnote.messenger.util.AppPerms;
import biz.dealnote.messenger.util.AssertUtils;
import biz.dealnote.messenger.util.Logger;
import biz.dealnote.messenger.util.MainActivityTransforms;
import biz.dealnote.messenger.util.Pair;
import biz.dealnote.messenger.util.PhoenixToast;
import biz.dealnote.messenger.util.RxUtils;
import biz.dealnote.messenger.util.StatusbarUtil;
import biz.dealnote.messenger.util.Utils;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static biz.dealnote.messenger.util.Objects.isNull;
import static biz.dealnote.messenger.util.Objects.nonNull;

public class MainActivity extends AppCompatActivity implements AdditionalNavigationFragment.NavigationDrawerCallbacks,
        OnSectionResumeCallback, AppStyleable, PlaceProvider, ServiceConnection, BottomNavigationView.OnNavigationItemSelectedListener {

    public static final String ACTION_MAIN = "android.intent.action.MAIN";
    public static final String ACTION_CHAT_FROM_SHORTCUT = "biz.dealnote.messenger.ACTION_CHAT_FROM_SHORTCUT";
    public static final String ACTION_OPEN_PLACE = "biz.dealnote.messenger.activity.MainActivity.openPlace";
    public static final String ACTION_OPEN_AUDIO_PLAYER = "biz.dealnote.messenger.activity.MainActivity.openAudioPlayer";
    public static final String ACTION_SEND_ATTACHMENTS = "biz.dealnote.messenger.ACTION_SEND_ATTACHMENTS";
    public static final String ACTION_SWITH_ACCOUNT = "biz.dealnote.messenger.ACTION_SWITH_ACCOUNT";
    public static final String ACTION_OPEN_WALL = "biz.dealnote.messenger.ACTION_OPEN_WALL";

    public static final String EXTRA_NO_REQUIRE_PIN = "no_require_pin";

    /**
     * Extra with type {@link biz.dealnote.messenger.model.ModelsBundle} only
     */
    public static final String EXTRA_INPUT_ATTACHMENTS = "input_attachments";
    protected static final int DOUBLE_BACK_PRESSED_TIMEOUT = 2000;
    private static final String TAG = "MainActivity_LOG";
    private static final int REQUEST_LOGIN = 101;
    private static final int REQUEST_CODE_CLOSE = 102;
    private static final int REQUEST_ENTER_PIN = 103;
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private final List<Action<MainActivity>> postResumeActions = new ArrayList<>(0);
    protected int mAccountId;
    protected int mLayoutRes = R.layout.activity_main;
    protected long mLastBackPressedTime;
    /**
     * Атрибуты секции, которая на данный момент находится на главном контейнере экрана
     */
    private AbsMenuItem mCurrentFrontSection;
    private Toolbar mToolbar;
    private BottomNavigationView mBottomNavigation;
    private ViewGroup mBottomNavigationContainer;
    private FragmentContainerView mViewFragment;
    private final FragmentManager.OnBackStackChangedListener mOnBackStackChangedListener = () -> {
        resolveToolbarNavigationIcon();
        keyboardHide();
    };
    private FragmentContainerView mMiniPlayer;
    private MusicUtils.ServiceToken mAudioPlayServiceToken;
    private boolean mDestroyed;
    /**
     * First - DrawerItem, second - Clear back stack before adding
     */
    private Pair<AbsMenuItem, Boolean> mTargetPage;
    private boolean resumed;

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public static boolean checkPlayServices(Context context) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }

    protected @MainActivityTransforms
    int getMainActivityTransform() {
        return MainActivityTransforms.MAIN;
    }

    private void postResume(Action<MainActivity> action) {
        if (resumed) {
            action.call(this);
        } else {
            postResumeActions.add(action);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(Settings.get().ui().getMainTheme());
        getDelegate().applyDayNight();

        super.onCreate(savedInstanceState);
        mDestroyed = false;

        mCompositeDisposable.add(Settings.get()
                .accounts()
                .observeChanges()
                .observeOn(Injection.provideMainThreadScheduler())
                .subscribe(this::onCurrentAccountChange));

        mCompositeDisposable.add(Stores.getInstance()
                .dialogs()
                .observeUnreadDialogsCount()
                .filter(pair -> pair.getFirst() == mAccountId)
                .compose(RxUtils.applyObservableIOToMainSchedulers())
                .subscribe(pair -> updateMessagesBagde(pair.getSecond())));

        bindToAudioPlayService();

        setContentView(mLayoutRes);

        mAccountId = Settings.get()
                .accounts()
                .getCurrent();

        setStatusbarColored(true, Settings.get().ui().isDarkModeEnabled(this));

        mBottomNavigation = findViewById(R.id.bottom_navigation_menu);
        mBottomNavigation.setOnNavigationItemSelectedListener(this);

        mBottomNavigationContainer = findViewById(R.id.bottom_navigation_menu_container);
        mMiniPlayer = findViewById(R.id.bottom_mini_player);
        mViewFragment = findViewById(R.id.fragment);

        getSupportFragmentManager().addOnBackStackChangedListener(mOnBackStackChangedListener);
        resolveToolbarNavigationIcon();

        updateMessagesBagde(Stores.getInstance()
                .dialogs()
                .getUnreadDialogsCount(mAccountId));

        if (isNull(savedInstanceState)) {
            boolean intentWasHandled = handleIntent(getIntent());

            if (!intentWasHandled) {
                Place place = Settings.get().ui().getDefaultPage(mAccountId);
                place.tryOpenWith(this);
            }
            checkFCMRegistration();

            if (!isAuthValid()) {
                startAccountsActivity();
            } else {
                if (getMainActivityTransform() == MainActivityTransforms.MAIN) {

                    mCompositeDisposable.add(InteractorFactory.createAudioInteractor().PlaceToAudioCache(this)
                            .compose(RxUtils.applyCompletableIOToMainSchedulers())
                            .subscribe(RxUtils.dummy(), t -> {/*TODO*/}));

                    CheckMusicInPC();
                    CheckUpdate();

                    if (Settings.get().other().isDelete_cache_images()) {
                        PreferencesFragment.CleanImageCache(this, false);
                    }
                }

                UpdateNotificationCount(mAccountId);
                boolean needPin = Settings.get().security().isUsePinForEntrance()
                        && !getIntent().getBooleanExtra(EXTRA_NO_REQUIRE_PIN, false);
                if (needPin) {
                    startEnterPinActivity();
                }
            }
        }
    }

    public void UpdateNotificationCount(int account) {
        mCompositeDisposable.add(new CountersInteractor(Injection.provideNetworkInterfaces()).getApiCounters(account)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(this::updateNotificationsBagde, t -> removeNotificationsBagde()));
    }

    private void CheckMusicInPC() {
        if (!AppPerms.hasReadWriteStoragePermision(this))
            return;
        File audios = new File(Settings.get().other().getMusicDir(), "audio_downloads.json");
        if (!audios.exists())
            return;
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(audios), StandardCharsets.UTF_8));
            reader.beginArray();
            while (reader.hasNext()) {
                MusicUtils.RemoteAudios.add(reader.nextString());
            }
        } catch (IOException ignore) {
            PhoenixToast.CreatePhoenixToast(this).showToastError(R.string.remote_audio_error);
        }
    }

    private void CheckUpdate() {
        if (!Constants.NEED_CHECK_UPDATE || !Settings.get().other().isAuto_update())
            return;
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(HttpLogger.DEFAULT_LOGGING_INTERCEPTOR).addInterceptor(chain -> {
                    Request request = chain.request().newBuilder().addHeader("User-Agent", Constants.USER_AGENT(null)).build();
                    return chain.proceed(request);
                });
        ProxyUtil.applyProxyConfig(builder, Injection.provideProxySettings().getActiveProxy());
        Request request = new Request.Builder()
                .url("https://raw.githubusercontent.com/umerov1999/Phoenix-for-VK/5.x/current_version.json").build();

        builder.build().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call th, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call th, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        int APK_VERS = Constants.VERSION_APK;
                        String Chngs = "";
                        JSONObject obj = new JSONObject(Objects.requireNonNull(response.body()).string());
                        if (obj.has("apk_version"))
                            APK_VERS = obj.getInt("apk_version");
                        if (obj.has("changes"))
                            Chngs = obj.getString("changes");

                        String apk_id = "null";
                        if (obj.has("app_id"))
                            apk_id = obj.getString("app_id");

                        String Chenges_log = Chngs;

                        if (APK_VERS <= Constants.VERSION_APK && Constants.APK_ID.equals(apk_id))
                            return;

                        Handler uiHandler = new Handler(getMainLooper());
                        uiHandler.post(() -> {
                            String res = "<i><a href=\"https://github.com/umerov1999/Phoenix-for-VK/releases/latest\">Скачать с github.com</a></i>";
                            res += ("<p>Изменения: " + Chenges_log + "</p>");

                            AlertDialog dlg = new MaterialAlertDialogBuilder(MainActivity.this)
                                    .setTitle("Обновление клиента")
                                    .setMessage(Html.fromHtml(res))
                                    .setPositiveButton("Закрыть", null)
                                    .setNegativeButton("Донатнуть", (dialog, which) -> {
                                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData clip = ClipData.newPlainText("response", "5599005042882048");
                                        clipboard.setPrimaryClip(clip);
                                        PhoenixToast.CreatePhoenixToast(MainActivity.this).setDuration(Toast.LENGTH_LONG).showToast("Номер карты скопирован в буфер");
                                    })

                                    .setCancelable(true)
                                    .create();
                            dlg.show();
                            try {
                                TextView tv = dlg.findViewById(android.R.id.message);
                                if (tv != null)
                                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                            } catch (Exception ignored) {
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        resumed = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumed = true;
        for (Action<MainActivity> action : postResumeActions) {
            action.call(this);
        }
        postResumeActions.clear();
    }

    private void startEnterPinActivity() {
        Intent intent = new Intent(this, EnterPinActivity.getClass(this));
        startActivityForResult(intent, REQUEST_ENTER_PIN);
    }

    private void checkFCMRegistration() {
        if (!checkPlayServices(this)) {
            if (!Settings.get().other().isDisabledErrorFCM()) {
                Utils.ThemedSnack(mViewFragment, getString(R.string.this_device_does_not_support_fcm), BaseTransientBottomBar.LENGTH_LONG)
                        .setAnchorView(mBottomNavigationContainer).setAction(R.string.button_access, v -> {
                    Settings.get().other().setDisableErrorFCM(true);
                }).show();
            }
            return;
        }

        IPushRegistrationResolver resolver = Injection.providePushRegistrationResolver();

        mCompositeDisposable.add(resolver.resolvePushRegistration()
                .compose(RxUtils.applyCompletableIOToMainSchedulers())
                .subscribe(RxUtils.dummy(), RxUtils.ignore()));

        //RequestHelper.checkPushRegistration(this);
    }

    private void bindToAudioPlayService() {
        if (!isActivityDestroyed() && mAudioPlayServiceToken == null) {
            mAudioPlayServiceToken = MusicUtils.bindToServiceWithoutStart(this, this);
        }
    }

    private void resolveToolbarNavigationIcon() {
        if (isNull(mToolbar)) return;

        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 1) {
            Drawable tr = AppCompatResources.getDrawable(this, R.drawable.arrow_left);
            Utils.setColorFilter(tr, CurrentTheme.getColorPrimary(this));
            mToolbar.setNavigationIcon(tr);
            mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        } else {
            if (!isFragmentWithoutNavigation()) {
                Drawable tr = AppCompatResources.getDrawable(this, R.drawable.phoenix_round);
                Utils.setColorFilter(tr, CurrentTheme.getColorPrimary(this));
                mToolbar.setNavigationIcon(tr);
                mToolbar.setNavigationOnClickListener(v -> {

                    ModalBottomSheetDialogFragment.Builder menus = new ModalBottomSheetDialogFragment.Builder();
                    menus.add(new OptionRequest(R.id.button_ok, getString(R.string.set_offline), R.drawable.offline));
                    menus.add(new OptionRequest(R.id.button_cancel, getString(R.string.open_clipboard_url), R.drawable.web));
                    menus.show(getSupportFragmentManager(), "left_options", option -> {
                        switch (option.getId()) {
                            case R.id.button_ok:
                                mCompositeDisposable.add(Injection.provideNetworkInterfaces().vkDefault(Settings.get().accounts().getCurrent()).account().setOffline()
                                        .compose(RxUtils.applySingleIOToMainSchedulers())
                                        .subscribe(this::OnSetOffline, t -> OnSetOffline(false)));
                                break;
                            case R.id.button_cancel:
                                ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                if (clipBoard != null && clipBoard.getPrimaryClip() != null && clipBoard.getPrimaryClip().getItemCount() > 0 && clipBoard.getPrimaryClip().getItemAt(0).getText() != null) {
                                    String temp = clipBoard.getPrimaryClip().getItemAt(0).getText().toString();
                                    LinkHelper.openUrl(this, mAccountId, temp);
                                }
                                break;
                        }
                    });
                });
            } else {
                Drawable tr = AppCompatResources.getDrawable(this, R.drawable.arrow_left);
                Utils.setColorFilter(tr, CurrentTheme.getColorPrimary(this));
                mToolbar.setNavigationIcon(tr);
                if (getMainActivityTransform() != MainActivityTransforms.SWIPEBLE) {
                    mToolbar.setNavigationOnClickListener(v -> openNavigationPage(AdditionalNavigationFragment.SECTION_ITEM_FEED));
                } else {
                    mToolbar.setNavigationOnClickListener(v -> onBackPressed());
                }
            }
        }
    }

    private void OnSetOffline(boolean succ) {
        if (succ)
            PhoenixToast.CreatePhoenixToast(this).showToast(R.string.succ_offline);
        else
            PhoenixToast.CreatePhoenixToast(this).showToastError(R.string.err_offline);
    }

    private void onCurrentAccountChange(int newAccountId) {
        mAccountId = newAccountId;
        Accounts.showAccountSwitchedToast(this);
        UpdateNotificationCount(newAccountId);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Logger.d(TAG, "onNewIntent, intent: " + intent);
        handleIntent(intent);
    }

    private boolean handleIntent(Intent intent) {
        if (intent == null) {
            return false;
        }

        if (ACTION_OPEN_WALL.equals(intent.getAction())) {
            int owner_id = intent.getExtras().getInt(Extra.OWNER_ID);
            PlaceFactory.getOwnerWallPlace(mAccountId, owner_id, null).tryOpenWith(this);
            return true;
        }

        if (ACTION_SWITH_ACCOUNT.equals(intent.getAction())) {
            int newAccountId = intent.getExtras().getInt(Extra.ACCOUNT_ID);
            Settings.get()
                    .accounts()
                    .setCurrent(newAccountId);

            mAccountId = newAccountId;
            intent.setAction(ACTION_MAIN);
        }

        Bundle extras = intent.getExtras();
        String action = intent.getAction();

        Logger.d(TAG, "handleIntent, extras: " + extras + ", action: " + action);

        if (extras != null && ActivityUtils.checkInputExist(this)) {
            mCurrentFrontSection = AdditionalNavigationFragment.SECTION_ITEM_DIALOGS;
            openNavigationPage(mCurrentFrontSection);
            return true;
        }

        if (ACTION_SEND_ATTACHMENTS.equals(action)) {
            mCurrentFrontSection = AdditionalNavigationFragment.SECTION_ITEM_DIALOGS;
            openNavigationPage(mCurrentFrontSection);
            return true;
        }

        if (ACTION_OPEN_PLACE.equals(action)) {
            Place place = intent.getParcelableExtra(Extra.PLACE);
            openPlace(place);
            if (place.type == Place.CHAT) {
                return Settings.get().ui().getSwipes_chat_mode() != SwipesChatMode.V2 || Settings.get().ui().getSwipes_chat_mode() == SwipesChatMode.DISABLED;
            }
            return true;
        }

        if (ACTION_OPEN_AUDIO_PLAYER.equals(action)) {
            openPlace(PlaceFactory.getPlayerPlace(mAccountId));
            return false;
        }

        if (ACTION_CHAT_FROM_SHORTCUT.equals(action)) {
            int aid = intent.getExtras().getInt(Extra.ACCOUNT_ID);
            int prefsAid = Settings.get()
                    .accounts()
                    .getCurrent();

            if (prefsAid != aid) {
                Settings.get()
                        .accounts()
                        .setCurrent(aid);
            }

            int peerId = intent.getExtras().getInt(Extra.PEER_ID);
            String title = intent.getStringExtra(Extra.TITLE);
            String imgUrl = intent.getStringExtra(Extra.IMAGE);

            Peer peer = new Peer(peerId).setTitle(title).setAvaUrl(imgUrl);
            PlaceFactory.getChatPlace(aid, aid, peer, 0).tryOpenWith(this);
            return Settings.get().ui().getSwipes_chat_mode() != SwipesChatMode.V2 || Settings.get().ui().getSwipes_chat_mode() == SwipesChatMode.DISABLED;
        }

        if (Intent.ACTION_VIEW.equals(action)) {
            Uri data = intent.getData();
            LinkHelper.openUrl(this, mAccountId, String.valueOf(data));
            return true;
        }

        return false;
    }

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        if (nonNull(mToolbar)) {
            mToolbar.setNavigationOnClickListener(null);
            mToolbar.setOnMenuItemClickListener(null);
        }

        super.setSupportActionBar(toolbar);

        mToolbar = toolbar;
        resolveToolbarNavigationIcon();
    }

    private void openChat(int accountId, int messagesOwnerId, @NonNull Peer peer, int Offset, boolean ChatOnly) {
        if (Settings.get().other().isEnable_show_recent_dialogs()) {
            RecentChat recentChat = new RecentChat(accountId, peer.getId(), peer.getTitle(), peer.getAvaUrl());
            getNavigationFragment().appendRecentChat(recentChat);
            getNavigationFragment().refreshNavigationItems();
            getNavigationFragment().selectPage(recentChat);
        }
        if (Settings.get().ui().getSwipes_chat_mode() == SwipesChatMode.DISABLED) {
            ChatFragment chatFragment = ChatFragment.Companion.newInstance(accountId, messagesOwnerId, peer);
            attachToFrontNoAnim(chatFragment);
        } else {
            if (Settings.get().ui().getSwipes_chat_mode() == SwipesChatMode.V2 && getMainActivityTransform() == MainActivityTransforms.MAIN) {
                Intent intent = new Intent(this, ChatActivity.class);
                intent.setAction(ChatActivity.ACTION_OPEN_PLACE);
                intent.putExtra(Extra.PLACE, PlaceFactory.getChatPlace(accountId, messagesOwnerId, peer, Offset));
                startActivity(intent);
            } else if (Settings.get().ui().getSwipes_chat_mode() == SwipesChatMode.V2 && getMainActivityTransform() != MainActivityTransforms.MAIN) {
                ChatFragment chatFragment = ChatFragment.Companion.newInstance(accountId, messagesOwnerId, peer);
                attachToFrontNoAnim(chatFragment);
            } else {
                if (!ChatOnly)
                    clearBackStack();
                DialogsTabsFragment chatFragment = DialogsTabsFragment.newInstance(accountId, messagesOwnerId, peer, Offset);
                attachToFrontNoAnim(chatFragment);
            }
        }
    }

    private void openRecentChat(RecentChat chat) {
        int accountId = mAccountId;
        int messagesOwnerId = mAccountId;
        openChat(accountId, messagesOwnerId, new Peer(chat.getPeerId()).setAvaUrl(chat.getIconUrl()).setTitle(chat.getTitle()), 0, true);
    }

    private void openTargetPage() {
        if (mTargetPage == null) {
            return;
        }

        AbsMenuItem item = mTargetPage.getFirst();
        boolean clearBackStack = mTargetPage.getSecond();

        if (item.equals(mCurrentFrontSection)) {
            return;
        }

        if (item.getType() == AbsMenuItem.TYPE_ICON) {
            openNavigationPage(item, clearBackStack);
        }

        if (item.getType() == AbsMenuItem.TYPE_RECENT_CHAT) {
            openRecentChat((RecentChat) item);
        }

        mTargetPage = null;
    }

    private AdditionalNavigationFragment getNavigationFragment() {
        FragmentManager fm = getSupportFragmentManager();
        return (AdditionalNavigationFragment) fm.findFragmentById(R.id.additional_navigation_menu);
    }

    private void openNavigationPage(@NonNull AbsMenuItem item) {
        openNavigationPage(item, true);
    }

    private void startAccountsActivity() {
        Intent intent = new Intent(this, AccountsActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN);
    }

    private void clearBackStack() {
        FragmentManager manager = getSupportFragmentManager();
        /*if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }*/

        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // TODO: 13.12.2017 Exception java.lang.IllegalStateException:Can not perform this action after onSaveInstanceState
        Logger.d(TAG, "Back stack was cleared");
    }

    private void openNavigationPage(@NonNull AbsMenuItem item, boolean clearBackStack) {
        if (item.getType() == AbsMenuItem.TYPE_RECENT_CHAT) {
            openRecentChat((RecentChat) item);
            return;
        }

        SectionMenuItem sectionDrawerItem = (SectionMenuItem) item;
        if (sectionDrawerItem.getSection() == AdditionalNavigationFragment.PAGE_ACCOUNTS) {
            startAccountsActivity();
            return;
        }

        mCurrentFrontSection = item;
        getNavigationFragment().selectPage(item);

        if (clearBackStack) {
            clearBackStack();
        }

        int aid = mAccountId;

//        PlaceFactory.getDialogsPlace(aid, aid, null)

        switch (sectionDrawerItem.getSection()) {
            case AdditionalNavigationFragment.PAGE_DIALOGS:
                openPlace(PlaceFactory.getDialogsPlace(aid, aid, null, 0));
                break;
            case AdditionalNavigationFragment.PAGE_FRIENDS:
                openPlace(PlaceFactory.getFriendsFollowersPlace(aid, aid, FriendsTabsFragment.TAB_ALL_FRIENDS, null));
                break;
            case AdditionalNavigationFragment.PAGE_GROUPS:
                openPlace(PlaceFactory.getCommunitiesPlace(aid, aid));
                break;
            case AdditionalNavigationFragment.PAGE_PREFERENSES:
                openPlace(PlaceFactory.getPreferencesPlace(aid));
                break;
            case AdditionalNavigationFragment.PAGE_MUSIC:
                openPlace(PlaceFactory.getAudiosPlace(aid, aid));
                break;
            case AdditionalNavigationFragment.PAGE_DOCUMENTS:
                openPlace(PlaceFactory.getDocumentsPlace(aid, aid, DocsListPresenter.ACTION_SHOW));
                break;
            case AdditionalNavigationFragment.PAGE_FEED:
                openPlace(PlaceFactory.getFeedPlace(aid));
                break;
            case AdditionalNavigationFragment.PAGE_NOTIFICATION:
                openPlace(PlaceFactory.getNotificationsPlace(aid));
                break;
            case AdditionalNavigationFragment.PAGE_PHOTOS:
                openPlace(PlaceFactory.getVKPhotoAlbumsPlace(aid, aid, IVkPhotosView.ACTION_SHOW_PHOTOS, null));
                break;
            case AdditionalNavigationFragment.PAGE_VIDEOS:
                openPlace(PlaceFactory.getVideosPlace(aid, aid, IVideosListView.ACTION_SHOW));
                break;
            case AdditionalNavigationFragment.PAGE_BOOKMARKS:
                openPlace(PlaceFactory.getBookmarksPlace(aid, FaveTabsFragment.TAB_PHOTOS));
                break;
            case AdditionalNavigationFragment.PAGE_SEARCH:
                openPlace(PlaceFactory.getSearchPlace(aid, SearchTabsFragment.TAB_PEOPLE));
                break;
            case AdditionalNavigationFragment.PAGE_NEWSFEED_COMMENTS:
                openPlace(PlaceFactory.getNewsfeedCommentsPlace(aid));
                break;
            default:
                throw new IllegalArgumentException("Unknown place!!! " + item);
        }
    }

    @Override
    public void onSheetItemSelected(AbsMenuItem item, boolean longClick) {
        if (mCurrentFrontSection != null && mCurrentFrontSection.equals(item)) {
            return;
        }

        mTargetPage = Pair.Companion.create(item, !longClick);
        //после закрытия бокового меню откроется данная страница
    }

    @Override
    public void onSheetClosed() {
        postResume(MainActivity::openTargetPage);
    }

    @Override
    protected void onDestroy() {
        mCompositeDisposable.dispose();
        mDestroyed = true;

        getSupportFragmentManager().removeOnBackStackChangedListener(mOnBackStackChangedListener);

        //if(!bNoDestroyServiceAudio)
        unbindFromAudioPlayService();
        super.onDestroy();
    }

    private void unbindFromAudioPlayService() {
        if (mAudioPlayServiceToken != null) {
            MusicUtils.unbindFromService(mAudioPlayServiceToken);
            mAudioPlayServiceToken = null;
        }
    }

    private boolean isAuthValid() {
        return mAccountId != ISettings.IAccountsSettings.INVALID_ID;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AppPerms.tryInterceptAppPermission(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_LOGIN:
                mAccountId = Settings.get()
                        .accounts()
                        .getCurrent();

                if (mAccountId == ISettings.IAccountsSettings.INVALID_ID) {
                    supportFinishAfterTransition();
                }
                break;

            case REQUEST_CODE_CLOSE:
                if (resultCode == RESULT_OK) {
                    finish();
                }
                break;
            case REQUEST_ENTER_PIN:
                if (resultCode != RESULT_OK) {
                    finish();
                }
                break;
        }
    }

    /*
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        SwipeTouchListener.getGestureDetector().onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
     */

    public void keyboardHide() {
        try {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception ignored) {

        }
    }

    private Fragment getFrontFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.fragment);
    }

    @Override
    public void onBackPressed() {
        if (getNavigationFragment().isSheetOpen()) {
            getNavigationFragment().closeSheet();
            return;
        }

        Fragment front = getFrontFragment();
        if (front instanceof BackPressCallback) {
            if (!(((BackPressCallback) front).onBackPressed())) {
                return;
            }
        }

        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            if (getMainActivityTransform() != MainActivityTransforms.SWIPEBLE) {
                if (isFragmentWithoutNavigation()) {
                    openNavigationPage(AdditionalNavigationFragment.SECTION_ITEM_FEED);
                    return;
                }
                if (isChatFragment()) {
                    openNavigationPage(AdditionalNavigationFragment.SECTION_ITEM_DIALOGS);
                    return;
                }
            }
            if (mLastBackPressedTime < 0
                    || mLastBackPressedTime + DOUBLE_BACK_PRESSED_TIMEOUT > System.currentTimeMillis()
                    || !Settings.get().main().isNeedDoublePressToExit()) {
                supportFinishAfterTransition();
                return;
            }

            mLastBackPressedTime = System.currentTimeMillis();
            Snackbar.make(mViewFragment, getString(R.string.click_back_to_exit), BaseTransientBottomBar.LENGTH_SHORT).setAnchorView(mBottomNavigationContainer).show();
        } else {
            super.onBackPressed();
        }
    }

    private boolean isChatFragment() {
        return getFrontFragment() instanceof ChatFragment;
    }

    private boolean isFragmentWithoutNavigation() {
        return getFrontFragment() instanceof CommentsFragment ||
                getFrontFragment() instanceof PostCreateFragment ||
                getFrontFragment() instanceof GifPagerFragment;
    }

    @Override
    public boolean onNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }

    /* Убрать выделение в боковом меню */
    private void resetNavigationSelection() {
        mCurrentFrontSection = null;
        getNavigationFragment().selectPage(null);
    }

    @Override
    public void onSectionResume(SectionMenuItem sectionDrawerItem) {
        getNavigationFragment().selectPage(sectionDrawerItem);

        switch (sectionDrawerItem.getSection()) {
            case AdditionalNavigationFragment.PAGE_FEED:
                mBottomNavigation.getMenu().getItem(0).setChecked(true);
                break;
            case AdditionalNavigationFragment.PAGE_SEARCH:
                mBottomNavigation.getMenu().getItem(1).setChecked(true);
                break;
            case AdditionalNavigationFragment.PAGE_DIALOGS:
                mBottomNavigation.getMenu().getItem(2).setChecked(true);
                break;
            case AdditionalNavigationFragment.PAGE_NOTIFICATION:
                mBottomNavigation.getMenu().getItem(3).setChecked(true);
                break;
            default:
                mBottomNavigation.getMenu().getItem(4).setChecked(true);
                break;
        }

        mCurrentFrontSection = sectionDrawerItem;
    }

    @Override
    public void onChatResume(int accountId, int peerId, String title, String imgUrl) {
        if (Settings.get().other().isEnable_show_recent_dialogs()) {
            RecentChat recentChat = new RecentChat(accountId, peerId, title, imgUrl);
            getNavigationFragment().appendRecentChat(recentChat);
            getNavigationFragment().refreshNavigationItems();
            getNavigationFragment().selectPage(recentChat);
            mCurrentFrontSection = recentChat;
        }
    }

    @Override
    public void onClearSelection() {
        resetNavigationSelection();
        mCurrentFrontSection = null;
    }

    private void attachToFront(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    private void attachToFrontNoAnim(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    @Override
    public void setStatusbarColored(boolean colored, boolean invertIcons) {
        int statusbarNonColored = CurrentTheme.getStatusBarNonColored(this);
        int statusbarColored = CurrentTheme.getStatusBarColor(this);

        if (Utils.hasLollipop()) {
            Window w = getWindow();
            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.setStatusBarColor(colored ? statusbarColored : statusbarNonColored);
            int navigationColor = colored ? CurrentTheme.getNavigationBarColor(this) : Color.BLACK;
            w.setNavigationBarColor(navigationColor);
        }

        if (Utils.hasMarshmallow()) {
            int flags = getWindow().getDecorView().getSystemUiVisibility();
            if (invertIcons) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            getWindow().getDecorView().setSystemUiVisibility(flags);

            StatusbarUtil.setCustomStatusbarDarkMode(this, invertIcons);
        }

        if (Utils.hasOreo()) {
            Window w = getWindow();
            int flags = getWindow().getDecorView().getSystemUiVisibility();
            if (invertIcons) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                w.getDecorView().setSystemUiVisibility(flags);
                w.setNavigationBarColor(Color.WHITE);
            } else {
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                w.getDecorView().setSystemUiVisibility(flags);
                @ColorInt
                int navigationColor = colored ?
                        CurrentTheme.getNavigationBarColor(this) : Color.BLACK;
                w.setNavigationBarColor(navigationColor);
            }
        }
    }

    @Override
    public void hideMenu(boolean hide) {
        if (hide) {
            getNavigationFragment().closeSheet();
            getNavigationFragment().blockSheet();
            mBottomNavigationContainer.setVisibility(View.GONE);
        } else {
            mBottomNavigationContainer.setVisibility(View.VISIBLE);
            getNavigationFragment().unblockSheet();
        }
    }

    @Override
    public void openMenu(boolean open) {
//        if (open) {
//            getNavigationFragment().openSheet();
//        } else {
//            getNavigationFragment().closeSheet();
//        }
    }

    @Override
    public void openPlace(Place place) {
        Bundle args = place.getArgs();
        switch (place.type) {
            case Place.VIDEO_PREVIEW:
                attachToFront(VideoPreviewFragment.newInstance(args));
                break;

            case Place.STORY_PLAYER:
                attachToFront(StoryPagerFragment.newInstance(args));
                break;

            case Place.FRIENDS_AND_FOLLOWERS:
                attachToFront(FriendsTabsFragment.newInstance(args));
                break;

            case Place.WIKI_PAGE:
            case Place.EXTERNAL_LINK:
                attachToFront(BrowserFragment.newInstance(args));
                break;

            case Place.DOC_PREVIEW:
                Document document = args.getParcelable(Extra.DOC);
                if (document != null && document.hasValidGifVideoLink()) {
                    int aid = args.getInt(Extra.ACCOUNT_ID);
                    ArrayList<Document> documents = new ArrayList<>(Collections.singletonList(document));

                    Bundle argsForGifs = GifPagerFragment.buildArgs(aid, documents, 0);
                    attachToFront(GifPagerFragment.newInstance(argsForGifs));
                } else {
                    attachToFront(DocPreviewFragment.newInstance(args));
                }
                break;

            case Place.WALL_POST:
                attachToFrontNoAnim(WallPostFragment.newInstance(args));
                break;

            case Place.COMMENTS:
                attachToFrontNoAnim(CommentsFragment.newInstance(place));
                break;

            case Place.WALL:
                attachToFrontNoAnim(AbsWallFragment.newInstance(args));
                break;

            case Place.CONVERSATION_ATTACHMENTS:
                attachToFront(ConversationFragmentFactory.newInstance(args));
                break;

            case Place.PLAYER:
                Fragment player = getSupportFragmentManager().findFragmentByTag("audio_player");
                if (player instanceof AudioPlayerFragment)
                    ((AudioPlayerFragment) player).dismiss();
                AudioPlayerFragment.newInstance(args).show(getSupportFragmentManager(), "audio_player");
                break;

            case Place.CHAT:
                Peer peer = args.getParcelable(Extra.PEER);
                AssertUtils.requireNonNull(peer);
                openChat(args.getInt(Extra.ACCOUNT_ID), args.getInt(Extra.OWNER_ID), peer, args.getInt(Extra.OFFSET), true);
                break;

            case Place.CHAT_DUAL:
                Peer peer1 = args.getParcelable(Extra.PEER);
                AssertUtils.requireNonNull(peer1);
                openChat(args.getInt(Extra.ACCOUNT_ID), args.getInt(Extra.OWNER_ID), peer1, args.getInt(Extra.OFFSET), false);
                break;

            case Place.SEARCH:
                attachToFront(SearchTabsFragment.newInstance(args));
                break;

            case Place.BUILD_NEW_POST:
                PostCreateFragment postCreateFragment = PostCreateFragment.newInstance(args);
                place.applyTargetingTo(postCreateFragment);
                attachToFront(postCreateFragment);
                break;

            case Place.DIALOGS_TUBS_TOUCH:
                Fragment ret = getFrontFragment();
                if (ret instanceof DialogsTabsFragment) {
                    ((DialogsTabsFragment) ret).DisableTouch(args.getBoolean(Extra.TYPE));
                }
                break;

            case Place.EDIT_COMMENT: {
                Comment comment = args.getParcelable(Extra.COMMENT);
                int accountId = args.getInt(Extra.ACCOUNT_ID);
                Integer commemtId = args.getInt(Extra.COMMENT_ID);
                CommentEditFragment commentEditFragment = CommentEditFragment.newInstance(accountId, comment, commemtId);
                place.applyTargetingTo(commentEditFragment);
                attachToFront(commentEditFragment);
                break;
            }

            case Place.EDIT_POST:
                PostEditFragment postEditFragment = PostEditFragment.newInstance(args);
                place.applyTargetingTo(postEditFragment);
                attachToFront(postEditFragment);
                break;

            case Place.REPOST:
                attachToFront(RepostFragment.obtain(place));
                break;

            case Place.DIALOGS:
                attachToFront(DialogsFragment.newInstance(
                        args.getInt(Extra.ACCOUNT_ID),
                        args.getInt(Extra.OWNER_ID),
                        args.getString(Extra.SUBTITLE),
                        args.getInt(Extra.OFFSET)
                ));
                break;

            case Place.FORWARD_MESSAGES:
                attachToFront(FwdsFragment.newInstance(args));
                break;

            case Place.TOPICS:
                attachToFront(TopicsFragment.newInstance(args));
                break;

            case Place.CHAT_MEMBERS:
                attachToFront(ChatUsersFragment.newInstance(args));
                break;

            case Place.COMMUNITIES:
                CommunitiesFragment communitiesFragment = CommunitiesFragment.newInstance(
                        args.getInt(Extra.ACCOUNT_ID),
                        args.getInt(Extra.USER_ID)
                );

                attachToFront(communitiesFragment);
                break;

            case Place.AUDIOS:
                attachToFront(AudiosTabsFragment.newInstance(args.getInt(Extra.ACCOUNT_ID), args.getInt(Extra.OWNER_ID)));
                break;

            case Place.MENTIONS:
                attachToFront(NewsfeedMentionsFragment.newInstance(args.getInt(Extra.ACCOUNT_ID), args.getInt(Extra.OWNER_ID)));
                break;

            case Place.AUDIOS_IN_ALBUM:
                attachToFront(AudiosFragment.newInstance(args.getInt(Extra.ACCOUNT_ID), args.getInt(Extra.OWNER_ID), args.getInt(Extra.ID), 1, args.getString(Extra.ACCESS_KEY)));
                break;

            case Place.SEARCH_BY_AUDIO:
                attachToFront(AudiosFragment.newInstance(args.getInt(Extra.ACCOUNT_ID), args.getInt(Extra.OWNER_ID), args.getInt(Extra.ID), 2, null));
                break;

            case Place.VIDEO_ALBUM:
                attachToFront(VideosFragment.newInstance(args));
                break;

            case Place.VIDEOS:
                attachToFront(VideosTabsFragment.newInstance(args));
                break;

            case Place.VK_PHOTO_ALBUMS:
                attachToFront(VKPhotoAlbumsFragment.newInstance(
                        args.getInt(Extra.ACCOUNT_ID),
                        args.getInt(Extra.OWNER_ID),
                        args.getString(Extra.ACTION),
                        args.getParcelable(Extra.OWNER), false
                ));
                break;

            case Place.VK_PHOTO_ALBUM:
                attachToFront(VKPhotosFragment.newInstance(args));
                break;

            case Place.VK_PHOTO_ALBUM_GALLERY:
                attachToFront(PhotoPagerFragment.newInstance(place.type, args));
                break;

            case Place.FAVE_PHOTOS_GALLERY:
                attachToFront(PhotoPagerFragment.newInstance(place.type, args));
                break;

            case Place.SIMPLE_PHOTO_GALLERY:
                attachToFront(PhotoPagerFragment.newInstance(place.type, args));
                break;

            case Place.VK_PHOTO_TMP_SOURCE:
                attachToFront(PhotoPagerFragment.newInstance(place.type, args));
                break;

            case Place.POLL:
                attachToFrontNoAnim(PollFragment.newInstance(args));
                break;

            case Place.BOOKMARKS:
                attachToFront(FaveTabsFragment.newInstance(args));
                break;

            case Place.DOCS:
                attachToFront(DocsFragment.newInstance(args));
                break;

            case Place.FEED:
                attachToFront(FeedFragment.newInstance(args));
                break;

            case Place.NOTIFICATIONS:
                if (Settings.get().accounts().getType(mAccountId).equals("vkofficial") || Settings.get().accounts().getType(mAccountId).equals("hacked")) {
                    attachToFront(AnswerVKOfficialFragment.newInstance(Settings.get().accounts().getCurrent()));
                    break;
                }
                attachToFront(FeedbackFragment.newInstance(args));
                break;

            case Place.PREFERENCES:
                attachToFront(PreferencesFragment.newInstance(args));
                break;

            case Place.RESOLVE_DOMAIN:
                ResolveDomainDialog domainDialog = ResolveDomainDialog.newInstance(args);
                domainDialog.show(getSupportFragmentManager(), "resolve-domain");
                break;

            case Place.VK_INTERNAL_PLAYER:
                Intent intent = new Intent(this, VideoPlayerActivity.class);
                intent.putExtras(args);
                startActivity(intent);
                break;

            case Place.NOTIFICATION_SETTINGS:
                attachToFrontNoAnim(new NotificationPreferencesFragment());
                break;

            case Place.LIKES_AND_COPIES:
                attachToFrontNoAnim(LikesFragment.newInstance(args));
                break;

            case Place.CREATE_PHOTO_ALBUM:
            case Place.EDIT_PHOTO_ALBUM:
                CreatePhotoAlbumFragment createPhotoAlbumFragment = CreatePhotoAlbumFragment.newInstance(args);
                place.applyTargetingTo(createPhotoAlbumFragment);
                attachToFront(createPhotoAlbumFragment);
                break;

            case Place.MESSAGE_LOOKUP:
                attachToFront(MessagesLookFragment.newInstance(args));
                break;

            case Place.GIF_PAGER:
                attachToFrontNoAnim(GifPagerFragment.newInstance(args));
                break;

            case Place.SECURITY:
                attachToFrontNoAnim(new SecurityPreferencesFragment());
                break;

            case Place.CREATE_POLL:
                CreatePollFragment createPollFragment = CreatePollFragment.newInstance(args);
                place.applyTargetingTo(createPollFragment);
                attachToFront(createPollFragment);
                break;

            case Place.COMMENT_CREATE:
                openCommentCreatePlace(place);
                break;

            case Place.LOGS:
                attachToFrontNoAnim(LogsFragement.newInstance());
                break;

            case Place.SINGLE_SEARCH:
                SingleTabSearchFragment singleTabSearchFragment = SingleTabSearchFragment.newInstance(args);
                attachToFront(singleTabSearchFragment);
                break;

            case Place.NEWSFEED_COMMENTS:
                NewsfeedCommentsFragment newsfeedCommentsFragment = NewsfeedCommentsFragment.newInstance(args.getInt(Extra.ACCOUNT_ID));
                attachToFront(newsfeedCommentsFragment);
                break;

            case Place.COMMUNITY_CONTROL:
                CommunityControlFragment communityControlFragment = CommunityControlFragment.newInstance(
                        args.getInt(Extra.ACCOUNT_ID),
                        args.getParcelable(Extra.OWNER),
                        args.getParcelable(Extra.SETTINGS)
                );
                attachToFront(communityControlFragment);
                break;

            case Place.COMMUNITY_INFO:
                CommunityInfoContactsFragment communityInfoFragment = CommunityInfoContactsFragment.newInstance(
                        args.getInt(Extra.ACCOUNT_ID),
                        args.getParcelable(Extra.OWNER)
                );
                attachToFront(communityInfoFragment);
                break;

            case Place.COMMUNITY_INFO_LINKS:
                CommunityInfoLinksFragment communityLinksFragment = CommunityInfoLinksFragment.newInstance(
                        args.getInt(Extra.ACCOUNT_ID),
                        args.getParcelable(Extra.OWNER)
                );
                attachToFrontNoAnim(communityLinksFragment);
                break;

            case Place.SETTINGS_THEME:
                ThemeFragment themes = ThemeFragment.newInstance();
                attachToFrontNoAnim(themes);
                if (getNavigationFragment().isSheetOpen()) {
                    getNavigationFragment().closeSheet();
                    return;
                }
                break;

            case Place.COMMUNITY_BAN_EDIT:
                CommunityBanEditFragment communityBanEditFragment = CommunityBanEditFragment.newInstance(
                        args.getInt(Extra.ACCOUNT_ID),
                        args.getInt(Extra.GROUP_ID),
                        (Banned) args.getParcelable(Extra.BANNED)
                );
                attachToFront(communityBanEditFragment);
                break;

            case Place.COMMUNITY_ADD_BAN:
                attachToFront(CommunityBanEditFragment.newInstance(
                        args.getInt(Extra.ACCOUNT_ID),
                        args.getInt(Extra.GROUP_ID),
                        args.getParcelableArrayList(Extra.USERS)
                ));
                break;

            case Place.COMMUNITY_MANAGER_ADD:
                attachToFront(CommunityManagerEditFragment.newInstance(
                        args.getInt(Extra.ACCOUNT_ID),
                        args.getInt(Extra.GROUP_ID),
                        args.getParcelableArrayList(Extra.USERS)
                ));
                break;

            case Place.COMMUNITY_MANAGER_EDIT:
                attachToFront(CommunityManagerEditFragment.newInstance(
                        args.getInt(Extra.ACCOUNT_ID),
                        args.getInt(Extra.GROUP_ID),
                        (Manager) args.getParcelable(Extra.MANAGER)
                ));
                break;

            case Place.REQUEST_EXECUTOR:
                attachToFront(RequestExecuteFragment.newInstance(args.getInt(Extra.ACCOUNT_ID)));
                break;

            case Place.USER_BLACKLIST:
                attachToFrontNoAnim(UserBannedFragment.newInstance(args.getInt(Extra.ACCOUNT_ID)));
                break;

            case Place.DRAWER_EDIT:
                attachToFrontNoAnim(DrawerEditFragment.newInstance());
                break;

            case Place.SINGLE_PHOTO:
                attachToFront(SinglePhotoFragment.newInstance(args));
                break;

            case Place.ARTIST:
                attachToFrontNoAnim(AudioCatalogFragment.newInstance(args));
                break;

            case Place.CATALOG_BLOCK_AUDIOS:
                attachToFront(AudiosInCatalogFragment.newInstance(args.getInt(Extra.ACCOUNT_ID), args.getString(Extra.ID), args.getString(Extra.TITLE)));
                break;

            case Place.CATALOG_BLOCK_PLAYLISTS:
                attachToFront(PlaylistsInCatalogFragment.newInstance(args.getInt(Extra.ACCOUNT_ID), args.getString(Extra.ID), args.getString(Extra.TITLE)));
                break;

            case Place.CATALOG_BLOCK_VIDEOS:
                attachToFront(VideosInCatalogFragment.newInstance(args.getInt(Extra.ACCOUNT_ID), args.getString(Extra.ID), args.getString(Extra.TITLE)));
                break;

            case Place.CATALOG_BLOCK_LINKS:
                attachToFront(LinksInCatalogFragment.newInstance(args.getInt(Extra.ACCOUNT_ID), args.getString(Extra.ID), args.getString(Extra.TITLE)));
                break;

            case Place.SHORT_LINKS:
                attachToFrontNoAnim(ShortedLinksFragment.newInstance(args.getInt(Extra.ACCOUNT_ID)));
                break;

            case Place.IMPORTANT_MESSAGES:
                attachToFrontNoAnim(ImportantMessagesFragment.newInstance(args.getInt(Extra.ACCOUNT_ID)));
                break;

            case Place.USER_DETAILS:
                int accountId = args.getInt(Extra.ACCOUNT_ID);
                User user = args.getParcelable(Extra.USER);
                UserDetails details = args.getParcelable("details");
                attachToFrontNoAnim(UserDetailsFragment.newInstance(accountId, user, details));
                break;

            case Place.WALL_ATTACHMENTS:
                Fragment wall_attachments = WallAttachmentsFragmentFactory.newInstance(args.getInt(Extra.ACCOUNT_ID), args.getInt(Extra.OWNER_ID), args.getString(Extra.TYPE));
                if (wall_attachments == null) {
                    throw new IllegalArgumentException("wall_attachments cant bee null");
                }
                attachToFront(wall_attachments);
                break;
            default:
                throw new IllegalArgumentException("Main activity can't open this place, type: " + place.type);
        }
    }

    private void openCommentCreatePlace(Place place) {
        CommentCreateFragment fragment = CommentCreateFragment.newInstance(
                place.getArgs().getInt(Extra.ACCOUNT_ID),
                place.getArgs().getInt(Extra.COMMENT_ID),
                place.getArgs().getInt(Extra.OWNER_ID),
                place.getArgs().getString(Extra.BODY)
        );

        place.applyTargetingTo(fragment);
        attachToFront(fragment);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        if (name.getClassName().equals(MusicPlaybackService.class.getName())) {
            Logger.d(TAG, "Connected to MusicPlaybackService");
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        if (isActivityDestroyed()) return;

        if (name.getClassName().equals(MusicPlaybackService.class.getName())) {
            Logger.d(TAG, "Disconnected from MusicPlaybackService");
            mAudioPlayServiceToken = null;
            bindToAudioPlayService();
        }
    }

    private boolean isActivityDestroyed() {
        return mDestroyed;
    }

    private void openPageAndCloseSheet(AbsMenuItem item) {
        if (getNavigationFragment().isSheetOpen()) {
            getNavigationFragment().closeSheet();
            onSheetItemSelected(item, false);
        } else {
            openNavigationPage(item);
        }
    }

    private void updateMessagesBagde(Integer count) {
        if (mBottomNavigation != null) {
            if (count > 0) {
                BadgeDrawable badgeDrawable = mBottomNavigation.getOrCreateBadge(R.id.menu_messages);
                badgeDrawable.setBackgroundColor(CurrentTheme.getColorPrimary(this));
                badgeDrawable.setBadgeTextColor(CurrentTheme.getColorOnPrimary(this));
                badgeDrawable.setNumber(count);
            } else {
                mBottomNavigation.removeBadge(R.id.menu_messages);
            }
        }
    }

    private void updateNotificationsBagde(SectionCounters counters) {
        if (mBottomNavigation != null) {
            if (counters.getNotifications() > 0) {
                BadgeDrawable badgeDrawable = mBottomNavigation.getOrCreateBadge(R.id.menu_feedback);
                badgeDrawable.setBackgroundColor(CurrentTheme.getColorPrimary(this));
                badgeDrawable.setBadgeTextColor(CurrentTheme.getColorOnPrimary(this));
                badgeDrawable.setNumber(counters.getNotifications());
            } else {
                mBottomNavigation.removeBadge(R.id.menu_feedback);
            }
            if (counters.getMessages() > 0) {
                BadgeDrawable badgeDrawable = mBottomNavigation.getOrCreateBadge(R.id.menu_messages);
                badgeDrawable.setBackgroundColor(CurrentTheme.getColorPrimary(this));
                badgeDrawable.setBadgeTextColor(CurrentTheme.getColorOnPrimary(this));
                badgeDrawable.setNumber(counters.getMessages());
            } else {
                mBottomNavigation.removeBadge(R.id.menu_messages);
            }
        }
    }

    private void removeNotificationsBagde() {
        if (mBottomNavigation != null) {
            mBottomNavigation.removeBadge(R.id.menu_feedback);
            mBottomNavigation.removeBadge(R.id.menu_messages);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_feed:
                openPageAndCloseSheet(AdditionalNavigationFragment.SECTION_ITEM_FEED);
                return true;
            case R.id.menu_search:
                openPageAndCloseSheet(AdditionalNavigationFragment.SECTION_ITEM_SEARCH);
                return true;
            case R.id.menu_messages:
                openPageAndCloseSheet(AdditionalNavigationFragment.SECTION_ITEM_DIALOGS);
                return true;
            case R.id.menu_feedback:
                openPageAndCloseSheet(AdditionalNavigationFragment.SECTION_ITEM_FEEDBACK);
                return true;
            case R.id.menu_other:
                if (getNavigationFragment().isSheetOpen()) {
                    getNavigationFragment().closeSheet();
                } else {
                    getNavigationFragment().openSheet();
                }
                return true;
        }
        return false;
    }
}
