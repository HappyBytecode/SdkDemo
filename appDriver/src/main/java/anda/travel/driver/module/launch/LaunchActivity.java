package anda.travel.driver.module.launch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.api.ApiConfig;
import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.baselibrary.utils.PermissionUtil;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.baselibrary.view.dialog.PermissionTipDialog;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.configurl.ConfigManager;
import anda.travel.driver.data.ad.AdvertisementEntity;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.module.login.LoginActivity;
import anda.travel.driver.module.main.MainActivity;
import anda.travel.driver.socket.SocketService;
import anda.travel.driver.util.SysConfigUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * 启动页
 */
public class LaunchActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    @Inject
    UserRepository mUserRepository;
    private CompositeSubscription mDisposable;

    private static final int RC_PERMS = 1026;

    @BindView(R2.id.container)
    FrameLayout container;
    @BindView(R2.id.launch_bg)
    View launch_bg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_launch);
        ButterKnife.bind(this);
        HxClientManager.getAppComponent().inject(this);
        mDisposable = new CompositeSubscription();
        /* 下载配置文件(包含司机协议和隐私政策) */
        new ConfigManager(this.getApplicationContext()).downloadFile(ApiConfig.getConfigUrl(), getPackageName());
        delayNavigateToNext();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
        SocketService.removeServiceListener(); //移除监听
    }

    /////根据登录状态判断跳转
    private void skip() {
        if (mUserRepository.isLogin()) {
            mDisposable.add(
                    mUserRepository.getUserInfo()
                            .compose(RxUtil.applySchedulers())
                            .subscribe(entity -> Timber.e("获取用户信息成功")
                                    , ex -> {
                                    })
            );
        }
        Class clz;
        if (mUserRepository.isLogin()) {
            clz = MainActivity.class;
        } else {
            clz = LoginActivity.class;
        }
        startActivity(new Intent(this, clz));
        finish();
    }

    @Override
    public int getBarColor() {
        return R.color.launch_title_color;
    }

    /**
     * 不在OnCreate方法中检查SocketService
     *
     * @return
     */
    @Override
    protected boolean checkServiceOnCreate() {
        return false;
    }

    private void delayNavigateToNext() {
        /* 获取配置信息 */
        reqSysConfig();
    }

    private void getAd() {
        mDisposable.add(mUserRepository.getAd()
                .compose(RxUtil.applySchedulers())
                .subscribe(this::initData,
                        throwable -> {
                            throwable.printStackTrace();
                            skip();
                        }));
    }

    /**
     * 获取配置信息
     */
    private void reqSysConfig() {
        // 校验是否需要弹出隐私更新
        mDisposable.add(
                mUserRepository.reqSysConfig()
                        .compose(RxUtil.applySchedulers())
                        .doOnNext(sysConfigEntity -> {
                            // 保存配置信息
                            SysConfigUtils.get().setSysConfig(sysConfigEntity);
                        })
                        .subscribe(entity -> {
                            /* 启动Service */
                            SocketService.startService(this, () -> Timber.d("onReady 触发！"));
                            getAd();
                        }, e -> {
                            /* 启动Service */
                            SocketService.startService(this, () -> Timber.d("onReady 触发！"));
                            // 请求失败也请求广告接口，否则页面不跳转
                            getAd();
                        })
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_PERMS)
    private void requestSomePermissions() {
        if (EasyPermissions.hasPermissions(this, PermissionUtil.getNeededPermission())) {
            delayNavigateToNext();
        } else {
            new PermissionTipDialog(this, () -> EasyPermissions.requestPermissions(
                    LaunchActivity.this,
                    "App需要使用权限,请您允许",
                    RC_PERMS,
                    PermissionUtil.getNeededPermission())).show();
        }
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        return false;
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        delayNavigateToNext();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initData(ArrayList<AdvertisementEntity> entities) {
        mUserRepository.saveAds(entities);
        skip();
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        ImmersionBar.with(this)
                .statusBarColor(R.color.white)
                .statusBarDarkFont(true)
                .fitsSystemWindows(true)
                .init();
    }
}
