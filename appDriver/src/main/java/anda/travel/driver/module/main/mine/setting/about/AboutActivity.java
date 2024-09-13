package anda.travel.driver.module.main.mine.setting.about;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;

import anda.travel.driver.BuildConfig;
import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.utils.VersionUtil;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.data.entity.SysConfigEntity;
import anda.travel.driver.util.SysConfigUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 关于页
 */
@SuppressLint("NonConstantResourceId")
public class AboutActivity extends BaseActivity {

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    @BindView(R2.id.tv_version)
    TextView mTvVersion;
    @BindView(R2.id.tv_phone)
    TextView mTvPhone;
    @BindView(R2.id.tv_introduce)
    TextView mTvIntroduce;
    @BindView(R2.id.tv_copyright)
    TextView mTvCopyright;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_about);
        ButterKnife.bind(this);
        HxClientManager.getAppComponent().inject(this); //依赖注入
        setTvVersion(); //显示版本号

        SysConfigEntity sysConfig = SysConfigUtils.get().getSysConfig();
        if (sysConfig != null) {
            mTvPhone.setText(sysConfig.getServerPhone()); //设置电话号
            mTvIntroduce.setText(sysConfig.getIntroduction()); //设置介绍内容
            mTvCopyright.setText(sysConfig.getCopyright()); //设置版权信息
        }
    }

    @OnClick({R2.id.tv_phone})
    public void onClick(View view) {
        if (view.getId() == R.id.tv_phone) {
            SysConfigUtils.get().dialServerPhone(this);
        }
    }

    private void setTvVersion() {
        mTvVersion.setText(String.format("V %s", BuildConfig.VERSION_NAME));
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
