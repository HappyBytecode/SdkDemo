package anda.travel.driver.module.login;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.socket.SocketService;
import anda.travel.driver.baselibrary.utils.ToastUtil;
import rx.subscriptions.CompositeSubscription;

/**
 * 登录页
 */
public class LoginActivity extends BaseActivity {

    private LoginFragment mLoginFragment;
    private CompositeSubscription mDisposable;
    @Inject
    UserRepository mUserRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_login);
        HxClientManager.getAppComponent().inject(this);
        if (mLoginFragment == null) {
            mLoginFragment = LoginFragment.newInstance();
            addFragment(R.id.fragment_container, mLoginFragment);
        }
        mDisposable = new CompositeSubscription();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof LoginFragment) {
            mLoginFragment = (LoginFragment) fragment;
        }
    }

    @Override
    public int getBarColor() {
        return android.R.color.transparent;
    }

    @Override
    protected boolean isNotLoginActivity() {
        return false; //是登录页
    }

    private long backTimeStamp; //按返回键的时间

    @Override
    public void onBackPressed() {
        long duration = System.currentTimeMillis() - backTimeStamp;
        if (Math.abs(duration) > 1000) {
            backTimeStamp = System.currentTimeMillis();
            ToastUtil.getInstance().toast(R.string.back_press_notice);
            return;
        }
        if (SocketService.getInstance() != null) {
            SocketService.getInstance().stopSelf(); //关闭Service
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
    }
}
