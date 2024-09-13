package anda.travel.driver.baselibrary.base;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.utils.ToastUtil;
import anda.travel.driver.baselibrary.view.SweetAlert.SweetAlertDialog;

public abstract class LibBaseActivity extends AppCompatActivity {

    private boolean mIsDialogCancelable;
    private SweetAlertDialog mDialog;
    private Handler mLoadingHlr;
    private final Runnable mLoadingRun = new Runnable() {
        @Override
        public void run() {
            if (mDialog == null) {
                mDialog = new SweetAlertDialog(LibBaseActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                mDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimary));
                mDialog.setTitleText("");
            }
            mDialog.setCancelable(mIsDialogCancelable);
            if (!isFinishing()) {
                mDialog.show();
            }
        }
    };

    private long mBtnClickStamp; //记录按键点击的时间戳

    /**
     * 按键是否在缓冲
     *
     * @return
     */
    public boolean isBtnBuffering() {
        long duration = System.currentTimeMillis() - mBtnClickStamp;
        mBtnClickStamp = System.currentTimeMillis(); //记录点击时间
        return Math.abs(duration) <= 400;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoadingView();
    }

    public void toast(int res) {
        ToastUtil.getInstance().toast(res);
    }

    public void toast(String msg) {
        ToastUtil.getInstance().toast(msg);
    }

    public SweetAlertDialog getDialog() {
        return mDialog;
    }

    public void showLoadingView(boolean cancelable) {
        showLoadingView(cancelable, 0); //默认延迟 400ms显示loading指示
    }

    public void hideLoadingView() {
        if (mLoadingHlr != null) mLoadingHlr.removeCallbacks(mLoadingRun);
        if (mDialog != null) mDialog.dismiss(); //隐藏Loading
    }

    public void showLoadingView(boolean cancelable, long delay) {
        mIsDialogCancelable = cancelable;

        if (mLoadingHlr == null) mLoadingHlr = new Handler();
        mLoadingHlr.postDelayed(mLoadingRun, delay);
    }

    public void showLoadingViewWithDelay(boolean cancelable) {
        showLoadingView(cancelable, 400); //默认延迟 400ms显示loading指示
    }

    /**
     * 添加 fragment
     *
     * @param idRes    容器ID
     * @param fragment 待添加的 fragment
     */
    protected void addFragment(@IdRes int idRes, Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(idRes, fragment);
        ft.commit();
    }

    /**
     * 替换 fragment
     *
     * @param idRes    容器ID
     * @param fragment 待替换的 fragment
     */
    protected void replaceFragment(@IdRes int idRes, Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(idRes, fragment);
        ft.commit();
    }

    /**
     * 隐藏fragment
     *
     * @param fragment 待替换的 fragment
     */
    protected void hideFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.hide(fragment);
        ft.commit();
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
