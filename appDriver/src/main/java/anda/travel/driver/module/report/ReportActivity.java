package anda.travel.driver.module.report;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.view.HeadViewWhite;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.util.SysConfigUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author moyuwan
 * @Date 18/3/6
 */
@SuppressLint("NonConstantResourceId")
public class ReportActivity extends BaseActivity {

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, ReportActivity.class);
        context.startActivity(intent);
    }

    @BindView(R2.id.head_view)
    HeadViewWhite mHeadView;
    @BindView(R2.id.tv_notice)
    TextView mTvNotice;
    @BindView(R2.id.iv_all_tick)
    ImageView mIvAllTick;
    @BindView(R2.id.iv_special_tick)
    ImageView mIvSpecialTick;

    @Inject
    UserRepository mUserRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_report);
        ButterKnife.bind(this);
        HxClientManager.getAppComponent().inject(this);
        mTvNotice.setText(SysConfigUtils.get().getOrderReportExplain(this));
        setDisplay(mUserRepository.isReportAll());
    }

    @OnClick({R2.id.cl_report_all, R2.id.cl_report_special})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.cl_report_all) {
            mUserRepository.setReportAll(true);
            setDisplay(true);
        } else if (id == R.id.cl_report_special) {
            mUserRepository.setReportAll(false);
            setDisplay(false);
        }
    }

    private void setDisplay(boolean isReportAll) {
        if (isReportAll) {
            visible(mIvAllTick);
            gone(mIvSpecialTick);
        } else {
            visible(mIvSpecialTick);
            gone(mIvAllTick);
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
