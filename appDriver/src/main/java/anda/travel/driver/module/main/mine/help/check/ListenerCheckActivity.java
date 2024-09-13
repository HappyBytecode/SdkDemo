package anda.travel.driver.module.main.mine.help.check;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.view.HeadViewWhite;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.data.entity.CheckResultEntity;
import anda.travel.driver.module.main.mine.help.check.dagger.DaggerListenerCheckComponent;
import anda.travel.driver.module.main.mine.help.check.dagger.ListenerCheckModule;
import anda.travel.driver.widget.CommonAlertDialog;
import anda.travel.driver.widget.wave.WaveView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 听单检测
 */
@SuppressLint("NonConstantResourceId")
public class ListenerCheckActivity extends BaseActivity implements ListenerCheckContract.View {

    @BindView(R2.id.head_view)
    HeadViewWhite mHeadView;
    @BindView(R2.id.tv_check)
    TextView mTvCheck;
    @BindView(R2.id.tv_notice)
    TextView mTvNotice;
    @BindView(R2.id.tvTag1)
    TextView mTvTag1;
    @BindView(R2.id.tvStatus1)
    TextView mTvStatus1;
    @BindView(R2.id.layoutTag1)
    LinearLayout mLayoutTag1;
    @BindView(R2.id.tvTag2)
    TextView mTvTag2;
    @BindView(R2.id.tvStatus2)
    TextView mTvStatus2;
    @BindView(R2.id.layoutTag2)
    LinearLayout mLayoutTag2;
    @BindView(R2.id.tvTag3)
    TextView mTvTag3;
    @BindView(R2.id.tvStatus3)
    TextView mTvStatus3;
    @BindView(R2.id.layoutTag3)
    LinearLayout mLayoutTag3;
    @BindView(R2.id.tv_stop)
    TextView mTvStop;
    @BindView(R2.id.tv_result_notice)
    TextView mTvResultNotice;
    @BindView(R2.id.tv_result1)
    TextView mTvResult1;
    @BindView(R2.id.tv_content1)
    TextView mTvContent1;
    @BindView(R2.id.tv_result2)
    TextView mTvResult2;
    @BindView(R2.id.tv_content2)
    TextView mTvContent2;
    @BindView(R2.id.tv_result3)
    TextView mTvResult3;
    @BindView(R2.id.tv_content3)
    TextView mTvContent3;
    @BindView(R2.id.tv_result4)
    TextView mTvResult4;
    @BindView(R2.id.tv_content4)
    TextView mTvContent4;
    @BindView(R2.id.tv_result5)
    TextView mTvResult5;
    @BindView(R2.id.tv_content5)
    TextView mTvContent5;
    @BindView(R2.id.layout_result)
    LinearLayout mLayoutResult;
    @BindView(R2.id.layout_result_notice)
    LinearLayout mLayoutResultNotice;
    @BindView(R2.id.wave_view)
    WaveView mWaveView;
    @BindView(R2.id.divLine1)
    View mDivLine1;
    @BindView(R2.id.divLine2)
    View mDiv2Line;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, ListenerCheckActivity.class);
        context.startActivity(intent);
    }

    @Inject
    ListenerCheckPresenter mPresenter;
    private boolean mIsChecking; //是否在"检测中"
    private List<TextView> mTitleList;
    private List<TextView> mContentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_listener_check);
        ButterKnife.bind(this);

        DaggerListenerCheckComponent.builder().appComponent(getAppComponent())
                .listenerCheckModule(new ListenerCheckModule(this))
                .build().inject(this);

        mTitleList = new ArrayList<>();
        mTitleList.add(mTvResult1);
        mTitleList.add(mTvResult2);
        mTitleList.add(mTvResult3);
        mTitleList.add(mTvResult4);
        mTitleList.add(mTvResult5);
        mContentList = new ArrayList<>();
        mContentList.add(mTvContent1);
        mContentList.add(mTvContent2);
        mContentList.add(mTvContent3);
        mContentList.add(mTvContent4);
        mContentList.add(mTvContent5);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public int getBarColor() {
        return R.color.lisenter_check_bar;
    }

    @Override
    public void showCheckInit() {
        mIsChecking = false;
        mHeadView.getLeftView().setVisibility(View.VISIBLE); //显示返回键
        mTvCheck.setEnabled(true);
        mTvCheck.setText(R.string.listener_check_btn_begin); //显示"开始检测"
        visible(mTvNotice);
        gone(mLayoutTag1, mLayoutTag2, mLayoutTag3, mTvStop, mDivLine1, mDiv2Line);
        stopAnimation();
    }

    @Override
    public void showCheckStart() {
        mIsChecking = true;
        mHeadView.getLeftView().setVisibility(View.GONE); //隐藏返回键
        mTvCheck.setEnabled(false);
        mTvCheck.setText(R.string.listener_check_btn_ongoing); //显示"检测中"
        gone(mTvNotice);
        mTvStatus1.setText(R.string.listener_check_status_ongoing);
        mTvStatus2.setText(R.string.listener_check_status_wait);
        mTvStatus3.setText(R.string.listener_check_status_wait);
        visible(mLayoutTag1, mLayoutTag2, mLayoutTag3, mTvStop, mDivLine1, mDiv2Line);
        startAnimation();
    }

    @Override
    public void showCheckStopAlert() {
        new CommonAlertDialog(this).builder()
                .setTitle("确认要停止检测吗？")
                .setPositiveButton("确定", v -> mPresenter.stopCheck())
                .setNegativeButton("取消", v -> {
                })
                .show();
    }

    @Override
    public void showCheckStep2() {
        mTvStatus2.setText(R.string.listener_check_status_ongoing);
    }

    @Override
    public void showCheckStep3() {
        mTvStatus3.setText(R.string.listener_check_status_ongoing);
    }

    @Override
    public void setStepResult1(int strRes, boolean isError) {
        mTvStatus1.setText(strRes);
        if (isError) {
            mTvStatus1.setTextColor(ContextCompat.getColor(this, R.color.listener_check_red));
        } else {
            mTvStatus1.setTextColor(ContextCompat.getColor(this, R.color.listener_check_green));
        }
    }

    @Override
    public void setStepResult2(int strRes, boolean isError) {
        mTvStatus2.setText(strRes);
        if (isError) {
            mTvStatus2.setTextColor(ContextCompat.getColor(this, R.color.listener_check_red));
        } else {
            mTvStatus2.setTextColor(ContextCompat.getColor(this, R.color.listener_check_green));
        }
    }

    @Override
    public void setStepResult3(int strRes, boolean isError) {
        mTvStatus3.setText(strRes);
        if (isError) {
            mTvStatus3.setTextColor(ContextCompat.getColor(this, R.color.listener_check_red));
        } else {
            mTvStatus3.setTextColor(ContextCompat.getColor(this, R.color.listener_check_green));
        }
    }

    @Override
    public void showResultError(List<CheckResultEntity> list) {
        mIsChecking = false;
        mHeadView.getLeftView().setVisibility(View.VISIBLE); //显示返回键
        mTvCheck.setText(R.string.listener_check_btn_finish); //显示"检测完毕"
        gone(mLayoutTag1, mLayoutTag2, mLayoutTag3, mTvStop, mDivLine1, mDiv2Line);
        mLayoutResult.setVisibility(View.VISIBLE);
        mTvResultNotice.setText(R.string.check_result_error);
        for (int i = 0; i < mTitleList.size(); i++) { //控制结果的显示
            boolean hasData = (i + 1) <= list.size();
            if (hasData) {
                CheckResultEntity entity = list.get(i);
                mTitleList.get(i).setVisibility(View.VISIBLE);
                mContentList.get(i).setVisibility(View.VISIBLE);
                mTitleList.get(i).setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_tingdan_notice, 0, 0, 0);
                mTitleList.get(i).setText(entity.errorTitle);
                mContentList.get(i).setText(entity.errorReason);
            } else {
                mTitleList.get(i).setVisibility(View.GONE);
                mContentList.get(i).setVisibility(View.GONE);
            }
        }
        mLayoutResultNotice.setVisibility(View.INVISIBLE);
        stopAnimation();
    }

    @Override
    public void showResultNormal(List<String> list) {
        mIsChecking = false;
        mHeadView.getLeftView().setVisibility(View.VISIBLE); //显示返回键
        mTvCheck.setText(R.string.listener_check_btn_finish); //显示"检测完毕"
        gone(mLayoutTag1, mLayoutTag2, mLayoutTag3, mTvStop, mDivLine1, mDiv2Line);
        mLayoutResult.setVisibility(View.VISIBLE);
        mTvResultNotice.setText(R.string.check_result_normal);
        for (int i = 0; i < mTitleList.size(); i++) { //控制结果的显示
            boolean hasData = (i + 1) <= list.size();
            if (hasData) {
                mTitleList.get(i).setVisibility(View.VISIBLE);
                mTitleList.get(i).setText(list.get(i));
            } else {
                mTitleList.get(i).setVisibility(View.GONE);
            }
        }
        mLayoutResultNotice.setVisibility(View.VISIBLE);
        stopAnimation();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void startAnimation() {
        mWaveView.start(); //开启动画
    }

    @Override
    public void stopAnimation() {
        mWaveView.stop(); //停止动画
    }

    @OnClick({R2.id.tv_check, R2.id.tv_stop})
    public void onClick(View view) {
        if (isBtnBuffering()) return;
        int id = view.getId();
        if (id == R.id.tv_check) {
            mPresenter.startCheck();
        } else if (id == R.id.tv_stop) {
            showCheckStopAlert();
        }
    }

    @Override
    public void onBackPressed() {
        if (mIsChecking) return;
        super.onBackPressed();
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
