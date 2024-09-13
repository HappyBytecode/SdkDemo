package anda.travel.driver.module.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialog;
import androidx.fragment.app.Fragment;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.baselibrary.utils.PermissionUtil;
import anda.travel.driver.baselibrary.utils.SP;
import anda.travel.driver.common.AppConfig;
import anda.travel.driver.common.AppManager;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.config.DutyStatus;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.data.duty.DutyRepository;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.event.NetworkEvent;
import anda.travel.driver.module.car.SelectCarActivity;
import anda.travel.driver.module.feedback.details.FeedBackDetailsActivity;
import anda.travel.driver.module.feedback.list.FeedBackListActivity;
import anda.travel.driver.module.main.duty.DutyFragment;
import anda.travel.driver.module.main.home.HomeFragment;
import anda.travel.driver.module.main.mine.help.feedback.FeedbackActivity;
import anda.travel.driver.module.order.popup.OrderPopupActivity;
import anda.travel.driver.socket.SocketEvent;
import anda.travel.driver.socket.SocketService;
import anda.travel.driver.util.JumpPermissionUtil;
import anda.travel.driver.util.Navigate;
import anda.travel.driver.util.ScreenCaptureManager;
import anda.travel.driver.util.SettingUtil;
import anda.travel.driver.util.SpeechUtil;
import anda.travel.driver.view.CommonBottomSheetDialog;
import anda.travel.driver.widget.BaseTipsDialog;
import anda.travel.driver.widget.CommonAlertDialog;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 主页
 *
 * @author Lenovo
 */
public class MainActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    private static final int RC_FROM_LOGIN_PERMS = 1001;
    private static final String PREF_BACKGROUND_AWAKE = "PREF_BACKGROUND_AWAKE";

    public static void actionStartFromService(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void actionStart(Context context, String notice) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(IConstants.REDISTRIBUTE_NOTICE, notice);
        context.startActivity(intent);
    }

    @Inject
    UserRepository mUserRepository;
    @Inject
    DutyRepository mDutyRepository;

    private NetworkChangeReceiver mReceiver;

    ScreenCaptureManager screenCaptureManager;

    private CommonAlertDialog gpsAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_main);
        ButterKnife.bind(this);
        Fragment frgHome = getSupportFragmentManager().findFragmentById(R.id.fl_main_home);
        HomeFragment homeFragment = HomeFragment.newInstance();
        if (frgHome == null) {
            addFragment(R.id.fl_main_home, homeFragment);
        }
        Fragment frgDuty = getSupportFragmentManager().findFragmentById(R.id.fl_main_duty);
        DutyFragment dutyFragment = DutyFragment.newInstance();
        if (frgDuty == null) {
            addFragment(R.id.fl_main_duty, dutyFragment);
        }
        HxClientManager.getAppComponent().inject(this);
        register();

        /* 延时检测网络 */
        new Handler().postDelayed(this::checkNetwork, 800);

        //处理Intent
        dealwithIntent(getIntent());
        //关闭省电优化
        checkBattery();
        // 打开后台弹出界面权限
        if (!SP.getInstance(getApplicationContext()).getBoolean(PREF_BACKGROUND_AWAKE, false)) {
            checkBackAwake();
        }
        checkGPSopen();
        findViewById(R.id.iv_back).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.iv_user_center).setOnClickListener(view -> Navigate.openUserCenter(this));
        findViewById(R.id.iv_message_list).setOnClickListener(view -> Navigate.openMessageCenter(this));
        screenCaptureManager = ScreenCaptureManager.Companion.newInstance(this);
        /////开启截屏监听
        screenCaptureManager.setListener(imagePath -> {
            BaseActivity baseActivity = (BaseActivity) AppManager.getInstance().currentActivity();
            if (baseActivity instanceof FeedbackActivity || baseActivity instanceof FeedBackListActivity
                    || baseActivity instanceof FeedBackDetailsActivity || baseActivity instanceof OrderPopupActivity) {
                AppConfig.setFloatView(false);
            } else {
                AppConfig.setFloatView(true);
                baseActivity.showFloatView();
            }
        });
        screenCaptureManager.start();
    }

    private void checkBackAwake() {
        if (!Build.MANUFACTURER.equals("HUAWEI")) {
            // 非华为手机且低于29才可设置
            if (Build.VERSION.SDK_INT < 29 && !JumpPermissionUtil.canBackgroundStart(this)) {
                new CommonAlertDialog(this).builder()
                        .setTitle("为保障应用处于后台时正常接单")
                        .setMsg("请前往设置界面授予权限\n<后台弹出界面>或<允许自动启动>\n")
                        .setPositiveButton("前往", v -> {
                            isBtnBuffering();
                            SP.getInstance(getApplicationContext()).putBoolean(PREF_BACKGROUND_AWAKE, true);
                            JumpPermissionUtil.toSelfSetting(this);
                        })
                        .setNegativeButton("取消", v -> {
                        })
                        .show();
            }
        }
    }

    private void checkBattery() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                if (pm != null && !pm.isIgnoringBatteryOptimizations(getPackageName())) {
                    //1.进入系统电池优化设置界面,把当前APP加入白名单
                    //startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));
                    //2.弹出系统对话框,把当前APP加入白名单(无需进入设置界面)
                    //在manifest添加权限 <uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"/>
                    Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkGPSopen() {
        if (SettingUtil.isOpen(this)) {
            return;
        }
        // 转到手机设置界面，用户设置GPS
        // 设置完成后返回到原来的界面
        if (gpsAlertDialog == null) {
            gpsAlertDialog = new CommonAlertDialog(this).builder()
                    .setTitle("打开GPS、WLAN和移动网络提升定位精准度，现在开启？")
                    .setPositiveButton("去设置", v -> {
                        // 转到手机设置界面，用户设置GPS
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                    })
                    .setNegativeButton("暂不开启", v -> {
                    });
        }
        if (!gpsAlertDialog.isShowing()) {
            gpsAlertDialog.show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //处理Intent
        dealwithIntent(intent);
    }

    /**
     * 改派提醒
     */
    private void dealwithIntent(Intent intent) {
        int typeFrom = intent.getIntExtra(IConstants.FROM_TYPE, 0);
        if (SelectCarActivity.TYPE_LOGIN == typeFrom) {
            requestFromLoginPermissions();
        }
        String notice = intent.getStringExtra(IConstants.REDISTRIBUTE_NOTICE);
        if (TextUtils.isEmpty(notice)) {
            return;
        }
        //语音播报
        SpeechUtil.speech(this, notice);
        BaseTipsDialog baseTipsDialog = new BaseTipsDialog(this);
        baseTipsDialog.setTipsTitle("改派成功");
        baseTipsDialog.setConfirmText(getResources().getString(R.string.iknow));
        baseTipsDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        mUserRepository.setIsOnSetting(false);
        if (mUserRepository.getScreenStatue()) {
            bright();
        } else {
            cancelBright();
        }
        super.onResume();
        //检查SocketService是否启动，如果已关闭，则重启
        SocketService.checkServiceWithStart(this);
        /* 开启长连接 */
        if (mUserRepository.isLogin()) {
            EventBus.getDefault().post(new SocketEvent(SocketEvent.CONNECT));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelBright();
        unregister();
        screenCaptureManager.stop();
    }

    private void register() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mReceiver = new NetworkChangeReceiver();
        registerReceiver(mReceiver, filter);
    }

    private void unregister() {
        unregisterReceiver(mReceiver);
    }

    /**
     * 监听网络变化
     */
    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkNetwork();
        }
    }

    private void checkNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean networkAvailable = networkInfo != null && networkInfo.isAvailable();
        mUserRepository.setNetworkStatus(!networkAvailable);
        if (networkAvailable) {
            EventBus.getDefault().post(new NetworkEvent(NetworkEvent.CONNECT));
        } else {
            EventBus.getDefault().post(new NetworkEvent(NetworkEvent.DISCONNECT));
        }
    }

    @Override
    protected boolean checkServiceOnCreate() {
        return false;
    }

    @Override
    public void onBackPressed() {
        HxClientManager.getInstance().reqOrderStatus((isOrdering, orderUuid) -> {
            if (isOrdering) {
                CommonBottomSheetDialog commonBottomSheetDialog = new CommonBottomSheetDialog.Builder(MainActivity.this, R.layout.hxyc_dialog_bottom_sheet_quit)
                        .setSubTitleText("系统检测到您当前有订单未完成，退出后后可能无法收到订单消息，确定要退出吗？")
                        .setConfirmListener(AppCompatDialog::dismiss).setCancelListener(dialog -> {
                            dialog.dismiss();
                            finish();
                        }).show();
                commonBottomSheetDialog.findViewById(R.id.iv_close)
                        .setOnClickListener(v -> commonBottomSheetDialog.dismiss());
            } else {
                if (mDutyRepository.getIsOnDuty() == DutyStatus.ON_DUTY_INT) {
                    CommonBottomSheetDialog commonBottomSheetDialog =
                            new CommonBottomSheetDialog.Builder(MainActivity.this, R.layout.hxyc_dialog_bottom_sheet_quit)
                                    .setSubTitleText("系统检测到您当前正在出车，退出后可能无法正常听单，确定要退出吗？")
                                    .setConfirmListener(AppCompatDialog::dismiss)
                                    .setCancelListener(dialog -> {
                                        dialog.dismiss();
                                        HxClientManager.getInstance().offDutyAndLoginOut();
                                        finish();
                                    }).show();
                    commonBottomSheetDialog.findViewById(R.id.iv_close)
                            .setOnClickListener(v -> commonBottomSheetDialog.dismiss());
                } else {
                    HxClientManager.getInstance().loginOut();
                    finish();
                }
            }
        });
    }

    @Override
    protected void onPause() {
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
        super.onPause();
    }

    //////////设置tab的颜色
    public void setTabColor(int color) {
        findViewById(R.id.head_view).setBackgroundColor(color);
    }

    @AfterPermissionGranted(RC_FROM_LOGIN_PERMS)
    private void requestFromLoginPermissions() {
        if (!EasyPermissions.hasPermissions(this, PermissionUtil.getNeededFormLoginPermission())) {
            EasyPermissions.requestPermissions(
                    MainActivity.this,
                    "App需要使用权限,请您允许",
                    RC_FROM_LOGIN_PERMS,
                    PermissionUtil.getNeededFormLoginPermission());
        }
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

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
    }
}
