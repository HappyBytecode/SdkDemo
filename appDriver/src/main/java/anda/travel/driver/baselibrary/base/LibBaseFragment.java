package anda.travel.driver.baselibrary.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;

import anda.travel.driver.baselibrary.utils.ToastUtil;

public class LibBaseFragment extends Fragment {
    protected View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mView;
    }

    @VisibleForTesting
    public View getView() {
        return mView;
    }

    /**
     * 按键是否在缓冲
     *
     * @return
     */
    public boolean isBtnBuffering() {
        if (getActivity() instanceof LibBaseActivity) {
            return ((LibBaseActivity) getActivity()).isBtnBuffering();
        } else {
            return false;
        }
    }

    public void showLoadingView(boolean cancelable) {
        showLoadingView(cancelable, 0);
    }

    public void showLoadingView(boolean cancelable, long delay) {
        if (!isAdded()) return;
        if (getActivity() instanceof LibBaseActivity) {
            ((LibBaseActivity) getActivity()).showLoadingView(cancelable, delay);
        }
    }

    public void showLoadingViewWithDelay(boolean cancelable) {
        showLoadingView(cancelable, 400); //默认延迟 400ms显示loading指示
    }

    public void hideLoadingView() {
        if (getActivity() instanceof LibBaseActivity) {
            ((LibBaseActivity) getActivity()).hideLoadingView();
        }
    }

    public void toast(String msg) {
        ToastUtil.getInstance().toast(msg);
    }

    public void toast(@StringRes int msgId) {
        ToastUtil.getInstance().toast(msgId);
    }

    public void visible(View... views) {
        for (View view : views) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public void invisible(View... views) {
        for (View view : views) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    public void gone(View... views) {
        for (View view : views) {
            view.setVisibility(View.GONE);
        }
    }
}
