package anda.travel.driver.module.main.mine.setting.platformRule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.gyf.immersionbar.ImmersionBar;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.utils.ToastUtil;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.configurl.MyConfig;
import anda.travel.driver.configurl.ParseUtils;
import anda.travel.driver.module.web.WebActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlatformRuleActivity extends BaseActivity {

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, PlatformRuleActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_platform_rule);
        ButterKnife.bind(this);
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

    @SuppressLint("NonConstantResourceId")
    @OnClick({R2.id.tv_terms, R2.id.tv_privacy})
    public void onClick(View view) {
        MyConfig config;
        int id = view.getId();
        if (id == R.id.tv_terms) {
            config = ParseUtils.getInstance().getMyConfig();
            if (config == null || TextUtils.isEmpty(config.getUserAgreement())) {
                ToastUtil.getInstance().toast("未获取到服务协议链接");
                return;
            }
            WebActivity.actionStart(this,
                    config.getUserAgreement(),
                    getResources().getString(R.string.login_terms_no_border));
        } else if (id == R.id.tv_privacy) {
            config = ParseUtils.getInstance().getMyConfig();
            if (config == null || TextUtils.isEmpty(config.getDriverPrivacyProtocol())) {
                ToastUtil.getInstance().toast("未获取到服务协议链接");
                return;
            }
            WebActivity.actionStart(this,
                    config.getDriverPrivacyProtocol(),
                    getResources().getString(R.string.login_privacy_title));
        }
    }
}