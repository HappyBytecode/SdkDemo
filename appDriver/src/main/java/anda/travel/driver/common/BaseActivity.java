package anda.travel.driver.common;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.CountDownTimer;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import anda.travel.driver.R;
import anda.travel.driver.event.DutyEvent;
import anda.travel.driver.util.Navigate;
import anda.travel.driver.view.CommonBottomSheetDialog;
import anda.travel.driver.widget.BaseTipsDialog;
import anda.travel.driver.widget.floatview.FloatingView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public abstract class BaseActivity extends BaseActivityWithoutIconics {
    protected FloatingView floatingView;
    protected Subscription mFloatSubscription;
    private CommonBottomSheetDialog mBottom_Dialog;/////底部弹框
    private CountDownTimer mCountDownTimer;

    @Override
    protected void onResume() {
        super.onResume();
        initImmersionBar();
        showFloatView();
    }

    public void showFloatView() {
        try {
            if (floatingView == null && AppConfig.isShowFloatView()) {
                floatingView = new FloatingView(this);
                floatingView.showFloat();
                floatingView.setOnClickListener(view -> {
                    Navigate.openFeedback(this);
                    AppConfig.setShowFloatTime(15);
                });
                if (AppConfig.getShowFloatTime() > 0) {
                    int pre = AppConfig.getShowFloatTime();
                    mFloatSubscription = Observable.interval(1, TimeUnit.SECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(aLong -> {
                                if (floatingView != null && !isFinishing() && aLong == pre) {
                                    AppConfig.setFloatView(false);
                                    floatingView.dismissFloatView();
                                    floatingView = null;
                                    AppConfig.setShowFloatTime(15);
                                    if (null != mFloatSubscription) {
                                        mFloatSubscription.unsubscribe();
                                    }
                                } else {
                                    AppConfig.setShowFloatTime(pre - aLong.intValue());
                                }
                            }, ex -> {
                            });
                }
            } else {
                if (!AppConfig.isShowFloatView()) {
                    if (floatingView != null) {
                        if (mFloatSubscription != null)
                            mFloatSubscription.unsubscribe();
                        floatingView.dismissFloatView();
                        floatingView = null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void initImmersionBar() {
    }

    public void showBaseTipDialog(String msg) {
        BaseTipsDialog mBaseTipsDialog = new BaseTipsDialog(this);
        mBaseTipsDialog.setTipsTitle("温馨提示");
        mBaseTipsDialog.setTipsContent(msg);
        mBaseTipsDialog.setConfirmText("我知道了");
        mBaseTipsDialog.show();
    }

    @SuppressLint("CheckResult")
    public void showGaoDeInService() {
        if (mBottom_Dialog != null) mBottom_Dialog.dismiss();
        mBottom_Dialog = new CommonBottomSheetDialog.Builder(this)
                .setTitleText(getString(R.string.duty_off_remind))
                .setSubTitleText(getString(R.string.gaode_in_service_remind))
                .setConfirmText(getString(R.string.duty_off))
                .setCancelText(getString(R.string.know) + "(10s)")
                .setConfirmListener(confirm -> {
                    //////司机点击确认收车
                    EventBus.getDefault().post(new DutyEvent(DutyEvent.OFF_DUTY));
                    confirm.dismiss();
                }).setCancelListener(Dialog::dismiss)
                .show();
        if (mCountDownTimer != null) mCountDownTimer.cancel();
        mCountDownTimer = new CountDownTimer(11 * 1000, 1000) {
            @Override
            public void onFinish() {

            }

            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished / 1000 != 0) {
                    mBottom_Dialog.setCancelText(getString(R.string.know) + "(" + millisUntilFinished / 1000 + "s)");
                } else {
                    cancel();
                    mBottom_Dialog.dismiss();
                }
            }
        }.start();
    }

    public void showGaoDeNotInService() {
        if (mBottom_Dialog != null) {
            mBottom_Dialog.dismiss();
        }

        new CommonBottomSheetDialog.Builder(this)
                .setTitleText(getString(R.string.duty_on_remind))
                .setSubTitleText(getString(R.string.gaode_not_in_service_remind))
                .setConfirmText(getString(R.string.work))
                .setCancelText(getString(R.string.cancel))
                .setConfirmListener(confirm -> {
                    //////司机点击确认出车
                    EventBus.getDefault().post(new DutyEvent(DutyEvent.ON_DUTY));
                    confirm.dismiss();
                }).setCancelListener(Dialog::dismiss)
                .show();
    }

    @SuppressLint("CheckResult")
    public void showAwake(String voice) {
        if (mBottom_Dialog != null) mBottom_Dialog.dismiss();
        String content;
        if (!TextUtils.isEmpty(voice)) {
            content = voice;
        } else {
            content = getString(R.string.work_awake_remind);
        }
        mBottom_Dialog = new CommonBottomSheetDialog.Builder(this)
                .setTitleText(getString(R.string.work_awake))
                .setSubTitleText(content)
                .setConfirmText(getString(R.string.work))
                .setCancelText(getString(R.string.know) + "(10s)")
                .setConfirmListener(confirm -> {
                    //////司机点击确认出车
                    EventBus.getDefault().post(new DutyEvent(DutyEvent.ON_DUTY));
                    confirm.dismiss();
                }).setCancelListener(Dialog::dismiss)
                .show();

        if (mCountDownTimer != null) mCountDownTimer.cancel();
        mCountDownTimer = new CountDownTimer(11 * 1000, 1000) {
            @Override
            public void onFinish() {
            }

            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished / 1000 != 0) {
                    mBottom_Dialog.setCancelText(getString(R.string.know) + "(" + millisUntilFinished / 1000 + "s)");
                } else {
                    cancel();
                    mBottom_Dialog.dismiss();
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        try {
            if (floatingView != null) {
                if (null != mFloatSubscription) {
                    mFloatSubscription.unsubscribe();
                }
                floatingView.dismissFloatView();
                floatingView = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        super.onDestroy();
    }

    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        if (resources != null
                && resources.getConfiguration() != null) {
            Configuration configuration = resources.getConfiguration();
            if (resources.getConfiguration().fontScale != 1.0f) {
                configuration.fontScale = 1.0f;
            }
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        return resources;
    }
}