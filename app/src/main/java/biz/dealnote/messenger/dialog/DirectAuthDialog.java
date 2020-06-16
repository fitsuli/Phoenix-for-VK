package biz.dealnote.messenger.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import biz.dealnote.messenger.Constants;
import biz.dealnote.messenger.Extra;
import biz.dealnote.messenger.R;
import biz.dealnote.messenger.api.PicassoInstance;
import biz.dealnote.messenger.fragment.base.BaseMvpDialogFragment;
import biz.dealnote.messenger.listener.TextWatcherAdapter;
import biz.dealnote.messenger.mvp.presenter.DirectAuthPresenter;
import biz.dealnote.messenger.mvp.view.IDirectAuthView;
import biz.dealnote.messenger.util.Objects;
import biz.dealnote.mvp.core.IPresenterFactory;

/**
 * Created by admin on 16.07.2017.
 * phoenix
 */
public class DirectAuthDialog extends BaseMvpDialogFragment<DirectAuthPresenter, IDirectAuthView> implements IDirectAuthView {

    public static final String ACTION_LOGIN_COMPLETE = "ACTION_LOGIN_COMPLETE";
    public static final String ACTION_LOGIN_VIA_WEB = "ACTION_LOGIN_VIA_WEB";
    public static final String ACTION_VALIDATE_VIA_WEB = "ACTION_VALIDATE_VIA_WEB";
    private EditText mLogin;
    private EditText mPassword;
    private EditText mCaptcha;
    private EditText mSmsCode;
    private TextView mValidate;
    private View mSmsCodeRoot;
    private View mContentRoot;
    private View mLoadingRoot;
    private View mCaptchaRoot;
    private ImageView mCaptchaImage;
    private View mEnterAppCodeRoot;
    private EditText mAppCode;

    public static DirectAuthDialog newInstance() {
        Bundle args = new Bundle();
        DirectAuthDialog fragment = new DirectAuthDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public DirectAuthDialog targetTo(Fragment fragment, int code) {
        setTargetFragment(fragment, code);
        return this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());

        View view = View.inflate(requireActivity(), R.layout.dialog_direct_auth, null);

        this.mLogin = view.findViewById(R.id.field_username);
        this.mLogin.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getPresenter().fireLoginEdit(s);
            }
        });

        this.mPassword = view.findViewById(R.id.field_password);
        this.mPassword.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getPresenter().firePasswordEdit(s);
            }
        });

        this.mEnterAppCodeRoot = view.findViewById(R.id.field_app_code_root);
        this.mAppCode = view.findViewById(R.id.field_app_code);
        this.mAppCode.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getPresenter().fireAppCodeEdit(s);
            }
        });

        /*
        this.mValidate = view.findViewById(R.id.button_validate_web);
        this.mValidate.setOnClickListener(view1 -> onValidate(getPresenter().GetRedirectUrl(), getPresenter().GetLogin(), getPresenter().GetPassword()));
         */

        view.findViewById(R.id.button_send_code_via_sms).setOnClickListener(view1 -> getPresenter().fireButtonSendCodeViaSmsClick());

        this.mSmsCodeRoot = view.findViewById(R.id.field_sms_code_root);
        this.mSmsCode = view.findViewById(R.id.field_sms_code);
        this.mSmsCode.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getPresenter().fireSmsCodeEdit(s);
            }
        });

        this.mContentRoot = view.findViewById(R.id.content_root);
        this.mLoadingRoot = view.findViewById(R.id.loading_root);
        this.mCaptchaRoot = view.findViewById(R.id.captcha_root);
        this.mCaptcha = view.findViewById(R.id.field_captcha);
        this.mCaptcha.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getPresenter().fireCaptchaEdit(s);
            }
        });
        this.mCaptchaImage = view.findViewById(R.id.captcha_img);

        builder.setView(view);
        builder.setPositiveButton(R.string.button_login, null);
        if (Constants.IS_HAS_LOGIN_WEB)
            builder.setNeutralButton(R.string.button_login_via_web, (dialogInterface, i) -> getPresenter().fireLoginViaWebClick());
        builder.setTitle(R.string.login_title);
        builder.setIcon(R.drawable.logo_vk);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        super.fireViewCreated();
        return dialog;
    }

    @NotNull
    @Override
    public IPresenterFactory<DirectAuthPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new DirectAuthPresenter(saveInstanceState);
    }

    public void onValidate(String url, String Login, String Password) {
        returnResultAndDissmiss(new Intent(ACTION_VALIDATE_VIA_WEB).putExtra(Extra.URL, url).putExtra(Extra.LOGIN, Login).putExtra(Extra.PASSWORD, Password).putExtra(Extra.TWOFA, "web_validation"), true);
    }

    @Override
    public void onResume() {
        super.onResume();

        Button buttonLogin = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
        buttonLogin.setOnClickListener(view -> getPresenter().fireLoginClick());
    }

    @Override
    public void setLoginButtonEnabled(boolean enabled) {
        Button buttonLogin = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);

        if (Objects.nonNull(buttonLogin)) {
            buttonLogin.setEnabled(enabled);
        }
    }

    @Override
    public void setSmsRootVisible(boolean visible) {
        if (Objects.nonNull(mSmsCodeRoot)) {
            mSmsCodeRoot.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void setAppCodeRootVisible(boolean visible) {
        if (Objects.nonNull(mEnterAppCodeRoot)) {
            mEnterAppCodeRoot.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void moveFocusToSmsCode() {
        if (Objects.nonNull(mSmsCode)) {
            mSmsCode.requestFocus();
        }
    }

    @Override
    public void moveFocusToAppCode() {
        if (Objects.nonNull(mSmsCode)) {
            mAppCode.requestFocus();
        }
    }

    @Override
    public void displayLoading(boolean loading) {
        if (Objects.nonNull(mLoadingRoot)) {
            mLoadingRoot.setVisibility(loading ? View.VISIBLE : View.GONE);
        }

        if (Objects.nonNull(mContentRoot)) {
            mContentRoot.setVisibility(loading ? View.INVISIBLE : View.VISIBLE);
        }
    }

    @Override
    public void setCaptchaRootVisible(boolean visible) {
        if (Objects.nonNull(mCaptchaRoot)) {
            mCaptchaRoot.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void displayCaptchaImage(String img) {
        if (Objects.nonNull(mCaptchaImage)) {
            PicassoInstance.with()
                    .load(img)
                    .placeholder(R.drawable.background_gray)
                    .into(mCaptchaImage);
        }
    }

    @Override
    public void moveFocusToCaptcha() {
        if (Objects.nonNull(mCaptcha)) {
            mCaptcha.requestFocus();
        }
    }

    @Override
    public void hideKeyboard() {
        try {
            InputMethodManager im = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(mLogin.getWindowToken(), 0);
            im.hideSoftInputFromWindow(mPassword.getWindowToken(), 0);
            im.hideSoftInputFromWindow(mCaptcha.getWindowToken(), 0);
            im.hideSoftInputFromWindow(mSmsCode.getWindowToken(), 0);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void returnSuccessToParent(int userId, String accessToken, String Login, String Password, String twoFA) {
        returnResultAndDissmiss(new Intent(ACTION_LOGIN_COMPLETE).putExtra(Extra.TOKEN, accessToken).putExtra(Extra.USER_ID, userId).putExtra(Extra.LOGIN, Login).putExtra(Extra.PASSWORD, Password).putExtra(Extra.TWOFA, twoFA), true);
    }

    private void returnResultAndDissmiss(Intent data, boolean Dismiss) {
        if (Objects.nonNull(getTargetFragment())) {
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
        }
        if (Dismiss)
            dismiss();
    }

    @Override
    public void returnLoginViaWebAction() {
        returnResultAndDissmiss(new Intent(ACTION_LOGIN_VIA_WEB), true);
    }
}