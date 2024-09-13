package anda.travel.driver.module.main.duty;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import java.text.MessageFormat;
import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.utils.PermissionUtil;
import anda.travel.driver.baselibrary.utils.SP;
import anda.travel.driver.common.BaseFragment;
import anda.travel.driver.config.AppValue;
import anda.travel.driver.config.RemindType;
import anda.travel.driver.config.WarnType;
import anda.travel.driver.data.entity.DriverEntity;
import anda.travel.driver.data.entity.OrderListenerEntity;
import anda.travel.driver.data.entity.WarningContentEntity;
import anda.travel.driver.module.car.SelectCarActivity;
import anda.travel.driver.module.main.duty.dagger.DaggerDutyComponent;
import anda.travel.driver.module.main.duty.dagger.DutyModule;
import anda.travel.driver.module.vo.OrderVO;
import anda.travel.driver.sound.SoundUtils;
import anda.travel.driver.util.Navigate;
import anda.travel.driver.util.SpeechUtil;
import anda.travel.driver.widget.BaseTipsDialog;
import anda.travel.driver.widget.CommonAlertDialog;
import anda.travel.driver.widget.RippleView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

/**
 * 功能描述："出车/收车/继续接单/暂停接单"
 */
@SuppressLint("NonConstantResourceId")
public class DutyFragment extends BaseFragment implements DutyContract.View, EasyPermissions.PermissionCallbacks {

    public static DutyFragment newInstance() {
        return new DutyFragment();
    }

    @BindView(R2.id.tv_setting)
    TextView mTvSetting;
    @BindView(R2.id.tv_off_duty)
    TextView mTvOffDuty;
    @BindView(R2.id.tv_on_duty)
    TextView mTvOnDuty;
    @BindView(R2.id.tv_status)
    TextView mTvStatus;
    @BindView(R2.id.ripple_view)
    RippleView mRippleView;

    @Inject
    DutyPresenter mPresenter;
    private boolean isNetworkDisconnect; //网络是否正常
    private boolean isOnDuty; //是否出车
    private BaseTipsDialog mWarningDialog;
    private static final int RC_ON_DUTY_PERMS = 1002;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.hxyc_fragment_duty, container, false);
        ButterKnife.bind(this, mView);
        mPresenter.onCreate();
        setRipple();
        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        DaggerDutyComponent.builder()
                .appComponent(getAppComponent())
                .dutyModule(new DutyModule(this))
                .build().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
        OrderListenerEntity setting = mPresenter.getListenerSetting();
        if (setting == null) { //模式
            mTvSetting.setText(R.string.duty_setting);
        } else {
            int remindType = setting.getRemindType();
            if (remindType == RemindType.REALTIME.getType()) { //仅实时
                mTvSetting.setText(R.string.duty_setting_real);
            } else if (remindType == RemindType.APPOINT.getType()) { //仅预约
                mTvSetting.setText(R.string.duty_setting_book);
            } else { //模式
                mTvSetting.setText(R.string.duty_setting);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.onDestory();
    }

    @Override
    public void openOrderSetting() {
        Navigate.openOrderSetting(getContext());
    }

    @Override
    public void getTokenSuccess(boolean isForce, String bizId, String token) {

    }

    @Override
    public void setOnDutyEnable(boolean isEnable) {
        mTvOnDuty.setEnabled(isEnable);
    }

    @Override
    public void showOnDuty() {
        isOnDuty = true;
        mTvOnDuty.setVisibility(View.GONE);
        mRippleView.setVisibility(View.VISIBLE);
        mRippleView.start();
        mTvStatus.setVisibility(View.VISIBLE);

        if (isNetworkDisconnect) {
            mTvStatus.setText(getString(R.string.duty_connecting));
        } else {
            mTvStatus.setText(getString(R.string.duty_listening));
        }
        mTvOffDuty.setVisibility(View.VISIBLE);
    }

    @Override
    public void showOffduty() {
        isOnDuty = false;
        mTvOnDuty.setVisibility(View.VISIBLE);
        Timber.d("%s", mPresenter.isFaceChecking);
        if (!mPresenter.isFaceChecking) {
            setOnDutyEnable(true);
        } else {
            mPresenter.isFaceChecking = false;
        }
        mTvOffDuty.setVisibility(View.GONE);
        mRippleView.setVisibility(View.GONE);
        mRippleView.stop();
        mTvStatus.setVisibility(View.GONE);
    }

    @Override
    public void openOrderPopup(String orderUuid, OrderVO vo) {
        Navigate.openOrderPopup(getContext(), orderUuid, vo);
    }

    @Override
    public void showWarningInfo(WarningContentEntity entity) {
        SpeechUtil.speech(getContext(), entity.getWarnContent());
        if (mWarningDialog != null) mWarningDialog.dismiss();
        mWarningDialog = new BaseTipsDialog(requireContext());
        mWarningDialog.setTipsTitle("系统警告");
        mWarningDialog.setTipsContent(entity.getWarnContent());
        mWarningDialog.setConfirmText(getString(R.string.iknow));
        mWarningDialog.setConfirmListener(() -> mPresenter.warnCallback(WarnType.READED, entity.getWarnUuid()));
        mWarningDialog.show();
    }

    @Override
    public void dismissWarningInfo() {
        if (mWarningDialog != null) {
            mWarningDialog.dismiss();
        }
    }

    /**
     * 判断应用是否处于后台
     *
     * @return
     */
    @Override
    public boolean isBackground() {
        ActivityManager activityManager = (ActivityManager) requireContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = null;
        if (activityManager != null) {
            appProcesses = activityManager.getRunningAppProcesses();
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(requireContext().getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Timber.d(MessageFormat.format("处于前台：{0}", appProcess.processName));
                    return false;
                } else {
                    Timber.d(MessageFormat.format("处于后台：{0}", appProcess.processName));
                    return true;
                }
            }
        }
        return true;
    }

    @Override
    public void showNetworkDisconnect(boolean isDisconnect) {
        isNetworkDisconnect = isDisconnect;
        if (!isOnDuty) return;
        if (isNetworkDisconnect) {
            mTvStatus.setEnabled(false);
            mTvStatus.setText(getString(R.string.duty_connecting));
        } else {
            mTvStatus.setEnabled(true);
            mTvStatus.setText(getString(R.string.duty_listening));
        }
    }

    @Override
    public void showForceDutyOnNotice(String notice) {
        String strNotice = TextUtils.isEmpty(notice)
                ? "同车司机正在出车，您仍要使用同一车辆出车吗？"
                : notice;
        new CommonAlertDialog(requireContext()).builder()
                .setTitle("出车冲突")
                .setMsg(strNotice)
                .setCancelable(true)
                .setPositiveButton("已交班", v -> {
                    isBtnBuffering(); //记录点击时间，避免其它按钮被快速点击
                    mPresenter.forceReqOnDuty(null);
                })
                .setNegativeButton("暂不出车", v -> {
                })
                .show();
    }

    @Override
    public void showForceOffDuty(String notice) {
        String strNotice = TextUtils.isEmpty(notice)
                ? "同车司机已出车，若您仍在使用本车，请联系对方后重新出车"
                : notice;
        SpeechUtil.speech(getContext(), strNotice); //语音播报
        BaseTipsDialog baseTipsDialog = new BaseTipsDialog(requireContext());
        baseTipsDialog.setTipsTitle("出车冲突");
        baseTipsDialog.setTipsContent(strNotice);
        baseTipsDialog.setConfirmText(getString(R.string.iknow));
        baseTipsDialog.setCancelable(false);
        baseTipsDialog.show();
    }

    @OnClick({R2.id.tv_setting, R2.id.tv_off_duty, R2.id.tv_on_duty, R2.id.tv_status})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_setting) {
            if (isBtnBuffering()) return;
            SoundUtils.getInstance().play(R.raw.speech_setting_listen);
            mPresenter.openOrderSetting();
        } else if (id == R.id.tv_off_duty) {
            if (isBtnBuffering()) return; //避免按键快速点击
            mPresenter.reqOffDuty(true, false);
        } else if (id == R.id.tv_on_duty) {
            if (isBtnBuffering()) return; //避免按键快速点击
            audioPermission();
        } else if (id == R.id.tv_status) {
            if (isBtnBuffering()) return; //避免按键快速点击
            mPresenter.testSystemPush(); //调试时使用
        }
    }

    @AfterPermissionGranted(RC_ON_DUTY_PERMS)
    public void audioPermission() {
        if (EasyPermissions.hasPermissions(requireContext(), PermissionUtil.getDutyNeededPermission(requireContext()))) {
            DriverEntity entity = mPresenter.getDriverEntityFromLocal();
            if (entity != null
                    && entity.substitute != null
                    && entity.substitute == AppValue.SUBSTITUTE.YES) { //是否是代班司机
                if (TextUtils.isEmpty(entity.vehicleNo)) { //没有绑定车辆
                    SelectCarActivity.actionStart(getContext(), SelectCarActivity.TYPE_DUTY);
                    return;
                }
            }
            mPresenter.reqOnDuty(null); //出车
        } else {
            EasyPermissions.requestPermissions(this, "为了正常接单，请开启权限",
                    RC_ON_DUTY_PERMS, PermissionUtil.getDutyNeededPermission(requireContext()));
            SP.getInstance(requireContext()).putBoolean("FIRST_PHONE_STATE", false);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        PermissionUtil.getPermissionSetTipDialog(perms, requireContext()).show();
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void setRipple() {
        mRippleView.setDuration(5000);
        mRippleView.setStyle(Paint.Style.FILL);
        mRippleView.setColor(ContextCompat.getColor(requireContext(), R.color.popup_item_choose));
        mRippleView.setInterpolator(new LinearOutSlowInInterpolator());
    }
}
