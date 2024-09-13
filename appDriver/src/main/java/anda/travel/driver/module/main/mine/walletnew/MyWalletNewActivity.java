package anda.travel.driver.module.main.mine.walletnew;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.tabs.TabLayout;
import com.gyf.immersionbar.ImmersionBar;

import java.text.MessageFormat;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.baselibrary.utils.NumberUtil;
import anda.travel.driver.baselibrary.utils.ToastUtil;
import anda.travel.driver.baselibrary.view.HeadView;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.data.entity.CashSettingEntity;
import anda.travel.driver.data.entity.MyWalletEntity;
import anda.travel.driver.data.entity.RentBillInfoEntity;
import anda.travel.driver.data.entity.WithdrawRuleEntity;
import anda.travel.driver.module.main.mine.walletnew.dagger.DaggerMyWalletNewComponent;
import anda.travel.driver.module.main.mine.walletnew.dagger.MyWalletNewModule;
import anda.travel.driver.util.FontUtils;
import anda.travel.driver.util.Navigate;
import anda.travel.driver.widget.EnhanceTabLayout;
import anda.travel.driver.widget.MyWalletCommonDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的钱包
 */
@SuppressLint("NonConstantResourceId")
public class MyWalletNewActivity extends BaseActivity implements MyWalletNewContract.View, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R2.id.swipe_refresh_layout)
    SwipeRefreshLayout swipe_refresh_layout;
    @BindView(R2.id.tv_balance)
    TextView tv_balance;
    @BindView(R2.id.tv_withdrawal)
    TextView tv_withdrawal;
    @BindView(R2.id.rl_withdrawal_record)
    View rl_withdrawal_record;
    @BindView(R2.id.rl_specification)
    View rl_specification;
    @BindView(R2.id.tv_notice)
    TextView mTvNotice;
    @BindView(R2.id.head_view)
    HeadView head_view;
    @BindView(R2.id.tab)
    EnhanceTabLayout tab;
    @BindView(R2.id.cons_balance_Top)
    ConstraintLayout mConsBalanceTop;
    @BindView(R2.id.cons_bill_Top)
    ConstraintLayout mConsBillTop;
    @BindView(R2.id.tv_can_withdrawal_num)
    TextView mTvCanWithdrawalNum;
    @BindView(R2.id.tv_to_be_recorder_num)
    TextView mTvToBeRecorderNum;
    @BindView(R2.id.tv_rent_bill_num)
    TextView mTvRentBillNum;
    @BindView(R2.id.tv_paid_rent_bill_num)
    TextView mTvPaidRentBillNum;
    @BindView(R2.id.tv_un_paid_num)
    TextView mTvUnPaidNum;
    @BindView(R2.id.ll_balance_bottom)
    LinearLayout mLlBalanceBottom;
    @BindView(R2.id.tv_bill_specification)
    TextView mTvBillSpecification;
    @BindView(R2.id.tv_check_bill_detail)
    TextView mTvCheckBillDetail;

    @Inject
    MyWalletNewPresenter mPresenter;

    private boolean isWithdrawClickable;
    private String remark;
    public String driverAccountUuid;//司机账户编号

    private int tabIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_my_wallet_new);
        ButterKnife.bind(this);
        DaggerMyWalletNewComponent.builder()
                .appComponent(HxClientManager.getAppComponent())
                .myWalletNewModule(new MyWalletNewModule(this))
                .build().inject(this);
        initView();
        if (mPresenter.getIsFirstMyWallet()) {
            new MyWalletCommonDialog(this, () -> mPresenter.saveIsFirstMyWallet(false)).show();
        }
    }

    private void initView() {
        mTvCheckBillDetail.setSelected(true);
        swipe_refresh_layout.setOnRefreshListener(this);
        swipe_refresh_layout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary));
        head_view.setDividingLineGone();
        tv_balance.setTypeface(FontUtils.getFontTypeFace(this));
        mTvCanWithdrawalNum.setTypeface(FontUtils.getFontTypeFace(this));
        mTvToBeRecorderNum.setTypeface(FontUtils.getFontTypeFace(this));
        mTvRentBillNum.setTypeface(FontUtils.getFontTypeFace(this));
        mTvPaidRentBillNum.setTypeface(FontUtils.getFontTypeFace(this));
        mTvUnPaidNum.setTypeface(FontUtils.getFontTypeFace(this));
        tab.addTab(getResources().getString(R.string.balance));
        tab.addTab(getResources().getString(R.string.bill));
        tab.setUnreadMsgVisible(0, getResources().getString(R.string.balance));
        tab.setUnreadMsgVisible(0, getResources().getString(R.string.bill));
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        mConsBalanceTop.setVisibility(View.VISIBLE);
                        mConsBillTop.setVisibility(View.GONE);
                        mLlBalanceBottom.setVisibility(View.VISIBLE);
                        mTvBillSpecification.setVisibility(View.GONE);
                        break;
                    case 1:
                        mConsBalanceTop.setVisibility(View.GONE);
                        mConsBillTop.setVisibility(View.VISIBLE);
                        mLlBalanceBottom.setVisibility(View.GONE);
                        mTvBillSpecification.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
                tabIndex = tab.getPosition();
                onRefresh();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        ImmersionBar.with(this)
                .statusBarColor(R.color.colorPrimaryDark)
                .statusBarDarkFont(false)
                .fitsSystemWindows(true)
                .init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.unsubscribe();
    }

    @OnClick({R2.id.rl_Balance_detail, R2.id.rl_withdrawal_record,
            R2.id.rl_specification, R2.id.tv_withdrawal, R2.id.img_head_left
            , R2.id.tv_check_bill_detail, R2.id.tv_to_be_recorder, R2.id.tv_bill_specification})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.rl_Balance_detail) {
            if (isBtnBuffering()) {
                return;
            }
            Navigate.openBalanceDetail(MyWalletNewActivity.this);
        } else if (id == R.id.rl_withdrawal_record) {
            if (isBtnBuffering()) {
                return;
            }
            Navigate.openWithdrawalRecord(MyWalletNewActivity.this);
        } else if (id == R.id.rl_specification) {
            if (isBtnBuffering()) {
                return;
            }
            Navigate.openRules(MyWalletNewActivity.this, false);
        } else if (id == R.id.tv_bill_specification) {
            if (isBtnBuffering()) {
                return;
            }
            Navigate.openRules(MyWalletNewActivity.this, true);
        } else if (id == R.id.tv_withdrawal) {
            if (isBtnBuffering()) {
                return;
            }
            if (isWithdrawClickable) {
                Navigate.openWithdrawalNew(MyWalletNewActivity.this
                        , mTvCanWithdrawalNum.getText().toString(), driverAccountUuid);
            } else {
                ToastUtil.getInstance().toast(
                        !TextUtils.isEmpty(remark) ? remark :
                                "当前无法提现，有疑问请联系司管");
            }
        } else if (id == R.id.img_head_left) {
            if (isBtnBuffering()) {
                return;
            }
            finish();
        } else if (id == R.id.tv_check_bill_detail) {
            if (isBtnBuffering()) {
                return;
            }
//                Navigate.openRentalBill(this, isDependDriver);
        } else if (id == R.id.tv_to_be_recorder) {
            if (isBtnBuffering()) {
                return;
            }
            mPresenter.reqGetNotAvailableMoneyDesc();
        }
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void onRefresh() {
        if (0 == tabIndex) {
            mPresenter.reqMyWallet();
        } else if (1 == tabIndex) {
            mPresenter.reqRentBillInfo();
        }
    }

    @Override
    public void showCashSetting(CashSettingEntity entity) {
        tv_balance.setText(entity.getStrBalance());
        isWithdrawClickable = entity.isWithdrawClickable();
        tv_withdrawal.setSelected(isWithdrawClickable);
        String remark;
        if (TextUtils.isEmpty(entity.getRemark())) {
            remark = "每周三8:00-18:00可以申请提现";
        } else {
            remark = entity.remark;
        }
        mTvNotice.setText(remark);
    }

    @Override
    public void showMyWallet(MyWalletEntity myWalletEntity) {
        tv_balance.setText(myWalletEntity.getStrBalance());
        isWithdrawClickable = myWalletEntity.isWithdrawClickable();
        tv_withdrawal.setSelected(isWithdrawClickable);
        mTvNotice.setText(myWalletEntity.title);
        remark = myWalletEntity.remark;
        driverAccountUuid = myWalletEntity.driverAccountUuid;
        mTvCanWithdrawalNum.setText(NumberUtil.getFormatValue(myWalletEntity.availableMoney));
        mTvToBeRecorderNum.setText(NumberUtil.getFormatValue(myWalletEntity.notAvailableMoney));
        tab.setUnreadMsgVisible(myWalletEntity.isNewBill, "账单");
    }

    @Override
    public void showRentBillInfo(RentBillInfoEntity rentBillInfoEntity) {
        mTvRentBillNum.setText(MessageFormat.format("{0}", rentBillInfoEntity.getSum()));
        mTvPaidRentBillNum.setText(MessageFormat.format("{0}", rentBillInfoEntity.getIsPay()));
        mTvUnPaidNum.setText(MessageFormat.format("{0}", rentBillInfoEntity.getIsNotPay()));
    }

    @Override
    public void showNotAvailableMoneyDesc(WithdrawRuleEntity ruleEntity) {
        if (null != ruleEntity) {
            new MyWalletCommonDialog(this, () -> {
            }).setSubTitle("")
                    .setTitle("待入账金额")
                    .setHtmlContent(ruleEntity.getContent())
                    .show();
        }
    }

    @Override
    public void refreshComplete() {
        swipe_refresh_layout.setRefreshing(false);
    }

}
