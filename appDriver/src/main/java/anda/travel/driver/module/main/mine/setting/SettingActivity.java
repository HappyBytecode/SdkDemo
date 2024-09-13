package anda.travel.driver.module.main.mine.setting;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.gyf.immersionbar.ImmersionBar;
import com.kyleduo.switchbutton.SwitchButton;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.BuildConfig;
import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.baselibrary.utils.SP;
import anda.travel.driver.baselibrary.utils.ToastUtil;
import anda.travel.driver.baselibrary.utils.TypeUtil;
import anda.travel.driver.baselibrary.utils.VersionUtil;
import anda.travel.driver.baselibrary.utils.file.FileUtil;
import anda.travel.driver.baselibrary.view.HeadViewWhite;
import anda.travel.driver.common.AppConfig;
import anda.travel.driver.common.AppManager;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.config.AppValue;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.data.entity.DriverEntity;
import anda.travel.driver.module.car.SelectCarActivity;
import anda.travel.driver.module.main.mine.setting.about.AboutActivity;
import anda.travel.driver.module.main.mine.setting.adpter.EnvDialogAdapter;
import anda.travel.driver.module.main.mine.setting.dagger.DaggerSettingComponent;
import anda.travel.driver.module.main.mine.setting.dagger.SettingModule;
import anda.travel.driver.module.main.mine.setting.platformRule.PlatformRuleActivity;
import anda.travel.driver.module.report.ReportActivity;
import anda.travel.driver.util.EnvFactory;
import anda.travel.driver.util.JumpPermissionUtil;
import anda.travel.driver.util.Navigate;
import anda.travel.driver.widget.CommonAlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ren.yale.android.cachewebviewlib.WebViewCacheInterceptorInst;
import rx.Observable;

/**
 * 我的-设置
 *
 * @author Lenovo
 */
@SuppressLint("NonConstantResourceId")
public class SettingActivity extends BaseActivity implements SettingContract.View {

    @BindView(R2.id.head_view)
    HeadViewWhite mHeadView;
    @BindView(R2.id.img_switch)
    SwitchButton mImgSwitch;
    @BindView(R2.id.tv_update)
    TextView mTvUpdate;
    @BindView(R2.id.tv_version_name)
    TextView mTvVersionName;
    @BindView(R2.id.tv_about)
    TextView mTvAbout;
    @BindView(R2.id.tv_exit)
    TextView mTvExit;
    @BindView(R2.id.layout_car)
    View mLayoutCar;
    @BindView(R2.id.tv_car)
    TextView mTvCar;
    @BindView(R2.id.tv_report)
    TextView mTvReport;
    @BindView(R2.id.tv_permission)
    TextView mTvPermission;
    @BindView(R2.id.tv_cache_size)
    TextView mTvCacheSize;
    @BindView(R2.id.tv_env)
    FrameLayout mTvEnv;
    @BindView(R2.id.tv_env_name)
    TextView mTvEnvName;
    @Inject
    SettingPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_setting);
        ButterKnife.bind(this);
        DaggerSettingComponent.builder()
                .appComponent(getAppComponent())
                .settingModule(new SettingModule(this))
                .build().inject(this);
        // 如华为 隐藏后台弹出窗口授权
        if (Build.MANUFACTURER.equals("HUAWEI") || Build.VERSION.SDK_INT > 28) {
            mTvPermission.setVisibility(View.GONE);
        }
        String audioPath = getExternalFilesDir(null).toString() + IConstants.AUDIO_FOLDER;
        FileUtil.makeDirs(audioPath);
        File audioFile = new File(audioPath);
        // 获取缓存大小
        mPresenter.getCacheSize(
                WebViewCacheInterceptorInst.getInstance().getCachePath(),
                Glide.getPhotoCacheDir(this),
                audioFile
        );
        /* 监听开关切换 */
        mImgSwitch.setCheckedImmediately(mPresenter.getScreenStatue());
        mImgSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                bright();
            } else {
                cancelBright();
            }
            mPresenter.setScreenStatue(isChecked);
        });
        // 是否可切换环境
        mTvEnv.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);
        mTvVersionName.setText(MessageFormat.format("V{0}", VersionUtil.getVerName(this)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
        mPresenter.setIsOnSetting(true);
        setCarDisplay();
        mTvReport.setText(mPresenter.isReportAll() ? R.string.report_all : R.string.report_special);
    }

    @Override
    protected void onDestroy() {
        mPresenter.setIsOnSetting(false);
        mPresenter.unsubscribe();
        super.onDestroy();
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R2.id.tv_update, R2.id.tv_about, R2.id.tv_exit, R2.id.layout_car, R2.id.layout_report, R2.id.tv_permission, R2.id.tv_clean_cache,
            R2.id.tv_env, R2.id.tv_platform_rule})
    public void onClick(View view) {
        if (isBtnBuffering()) {
            return;
        }
        int id = view.getId();
        if (id == R.id.tv_about) {
            AboutActivity.actionStart(this);
        } else if (id == R.id.tv_platform_rule) {
            PlatformRuleActivity.actionStart(this);
        } else if (id == R.id.tv_exit) {
            showDialog();
        } else if (id == R.id.layout_car) { //选择车辆
            SelectCarActivity.actionStart(this);
        } else if (id == R.id.layout_report) { //订单播报
            ReportActivity.actionStart(this);
        } else if (id == R.id.tv_permission) {
            JumpPermissionUtil.toSelfSetting(this);
        } else if (id == R.id.tv_clean_cache) {
            if (mTvCacheSize.getText().equals("暂无缓存")) {
                return;
            }
            Observable.create((Observable.OnSubscribe<String>) subscriber -> {
                Glide.get(this).clearDiskCache();
                WebViewCacheInterceptorInst.getInstance().clearCache();
                /////清除录音的缓存
                FileUtil.deleteFile(getExternalFilesDir(null).toString() + IConstants.AUDIO_FOLDER);
                subscriber.onNext("");
            }).compose(RxUtil.applySchedulers()).subscribe(cache -> {
                showCacheSize(cache);
                ToastUtil.getInstance().toast("清除缓存成功！");
            }, throwable -> ToastUtil.getInstance().toast("清除缓存失败，请重试"));
        } else if (id == R.id.tv_env) {
            int envCode = SP.getInstance(getApplicationContext()).getInt(IConstants.ENV);
            mTvEnvName.setText(EnvFactory.INSTANCE.createEnv(envCode).getEnvName());
            showBottomDialog();
        }
    }

    private void showBottomDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        @SuppressLint("InflateParams") View dialogView = getLayoutInflater().inflate(R.layout.hxyc_dialog_env_sheet, null);
        dialog.setContentView(dialogView);
        EnvDialogAdapter adapter = new EnvDialogAdapter(this);
        RecyclerView recyclerView = dialogView.findViewById(R.id.rv_env);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        List<String> list = new ArrayList<>();
        list.add("测试内网");
        list.add("测试外网");
        list.add("预生产");
        list.add("正式环境");
        adapter.addAll(list);
        adapter.setOnItemClickListener((position, view1, item) -> {
            toast("已切换至" + item + "，请重新进入App");
            dialog.dismiss();
            mPresenter.reqLogout();
            AppConfig.initConfig(getResources(), EnvFactory.INSTANCE.createEnv(position));
            SP.getInstance(getApplicationContext()).putInt(IConstants.ENV, position);
            new Handler().postDelayed(() -> {
                AppManager.getInstance().finishAllActivity();
                System.exit(0);
            }, 1000);
        });
        dialog.show();
    }

    private void showDialog() {
        new CommonAlertDialog(this).builder()
                .setTitle("确定退出该账户？")
                .setPositiveButton("确定", v -> {
                    isBtnBuffering(); //避免其它按键被快速点击添加
                    mPresenter.reqLogout();
                }).setNegativeButton("取消", v -> {

        }).setCancelable(false).show();
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void logoutSuccess() {
//        Navigate.openLogin(this); //跳转到登录页
        AppManager.getInstance().finishAllActivity(); //关闭所有界面（除登录页）
    }

    /**
     * 设置车辆显示
     */
    @Override
    public void setCarDisplay() {
        DriverEntity entity = mPresenter.getDriveEntity();
        if (entity == null) {
            return;
        }
        boolean isSubstitute = entity.substitute != null && entity.substitute == AppValue.SUBSTITUTE.YES;
        mLayoutCar.setVisibility(isSubstitute ? View.VISIBLE : View.GONE);
        mTvCar.setText(TypeUtil.getValue(entity.vehicleNo));
    }

    @Override
    public void showCacheSize(String size) {
        if (size.isEmpty()) {
            mTvCacheSize.setText("暂无缓存");
        } else {
            mTvCacheSize.setText(size);
        }
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        ImmersionBar.with(this)
                .statusBarColor(android.R.color.white)
                .statusBarDarkFont(true)
                .fitsSystemWindows(true)
                .init();
    }
}
