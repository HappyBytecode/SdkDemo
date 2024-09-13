package anda.travel.driver.module.login;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.network.RequestError;
import anda.travel.driver.baselibrary.network.RequestParams;
import anda.travel.driver.baselibrary.utils.RegUtil;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.config.AppValue;
import anda.travel.driver.config.HxErrorCode;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.config.LoginStatus;
import anda.travel.driver.data.dispatch.DispatchRepository;
import anda.travel.driver.data.duty.DutyRepository;
import anda.travel.driver.data.entity.AnalyzeDutyTime;
import anda.travel.driver.data.entity.DriverEntity;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.event.UIEvent;
import anda.travel.driver.event.UserEvent;
import anda.travel.driver.util.DeviceUtil;

/**
 * 功能描述：
 */
public class LoginPresenter extends BasePresenter implements LoginContract.Presenter {

    private final LoginContract.View mView;
    private final UserRepository mUserRepository;
    private final DispatchRepository mDispatchRepository;
    private final DutyRepository mDutyRepository;

    @Inject
    public LoginPresenter(LoginContract.View view,
                          UserRepository userRepository,
                          DispatchRepository dispatchRepository,
                          DutyRepository dutyRepository) {
        mView = view;
        mUserRepository = userRepository;
        mDispatchRepository = dispatchRepository;
        mDutyRepository = dutyRepository;
    }

    @SuppressLint("CheckResult")
    @Override
    public void reqLogin(final String phone, String pwd) {
        if (TextUtils.isEmpty(phone)) {
            mView.toast(R.string.login_empty_phone);
            return;
        }
        if (!RegUtil.isMobileNumber(phone)) {
            mView.toast(R.string.login_error_phone);
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            mView.toast(R.string.login_empty_pwd);
            return;
        }

        RequestParams.Builder builder = DeviceUtil.getDeviceInfo(mView.getContext());
        builder.putParam("mobile", phone);
        builder.putParam("password", pwd);
        mDisposable.add(mUserRepository.reqLoginNew(builder.build())
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingViewWithDelay(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(info -> {
                    DriverEntity driverEntity = info.driver;
                    saveAccount(phone);
                    if (driverEntity.isFirst.equals(LoginStatus.IS_FIRST)) {
                        /* 首次登录，需要先修改密码 */
                        mUserRepository.saveToken(driverEntity.token);
                        mView.loginIsFirst(phone);
                    } else {
                        /* 非首次登录，登录成功 */
                        if (IConstants.ALL_MODE_DRIVER.equals(driverEntity.identify)) {
                            mView.loginSuccess(driverEntity.substitute != null && driverEntity.substitute == AppValue.SUBSTITUTE.YES);
                        } else {

                        }
                    }
                }, ex -> {
                    if (ex instanceof RequestError) {
                        RequestError error = ((RequestError) ex);
                        if (error.getReturnCode() == HxErrorCode.DRIVER_ACCOUNT_INVALID) { //账号被封
                            mView.showAccountUnavailable(error.getMsg());
                            return;
                        }
                        if (error.getReturnCode() == HxErrorCode.ERROR_REGISTER_NO_PASSWORD) {
                            mView.loginIsFirst(phone);
                        }
                    }
                    showNetworkError(ex, R.string.network_error, mView, mUserRepository);
                }));
    }

    @Override
    public void saveAccount(String account) {
        mUserRepository.saveAccount(account);
    }

    @Override
    public String getAccount() {
        return mUserRepository.getAccount();
    }

    public void onCreate() {
        EventBus.getDefault().register(this);
        mDispatchRepository.destoryNavi();
        mDutyRepository.updateDutyTime(new AnalyzeDutyTime(false, System.currentTimeMillis(), 0));
        mDutyRepository.updateDutyLog(true, 0);
    }

    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onUIEvent(UIEvent event) {
        if (event.type == UIEvent.CLEAR_PWD) {
            mView.clearPwd();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(UserEvent event) {
        if (event.type == UserEvent.CHECK_AND_LOGIN) {
            mView.closeActivity(); //关闭界面
        }
    }
}
