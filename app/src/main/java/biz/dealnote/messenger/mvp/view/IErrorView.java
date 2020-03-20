package biz.dealnote.messenger.mvp.view;

import androidx.annotation.StringRes;

import biz.dealnote.messenger.util.PhoenixToast;

/**
 * Created by admin on 21.03.2017.
 * phoenix
 */
public interface IErrorView {
    void showError(String errorText);
    void showError(@StringRes int titleTes, Object... params);
    PhoenixToast getPhoenixToast();
}
