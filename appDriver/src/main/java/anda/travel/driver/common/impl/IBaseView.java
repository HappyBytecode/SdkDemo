package anda.travel.driver.common.impl;

import androidx.annotation.StringRes;

public interface IBaseView<T> {

    /**
     * 是否已经 add 到 Activity 中
     *
     * @return
     */
    boolean isActive();

    void showLoadingView(boolean cancelable);

    void showLoadingView(boolean cancelable, long delay);

    void showLoadingViewWithDelay(boolean cancelable);

    void hideLoadingView();

    void toast(String msg);

    void toast(@StringRes int msgId);

}