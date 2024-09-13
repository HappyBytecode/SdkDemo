package anda.travel.driver.module.main.mine.walletnew.withdrawal.withdrawalrecord.withdrawldetails;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.baselibrary.utils.NumberUtil;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.module.main.mine.walletnew.withdrawal.withdrawalrecord.withdrawldetails.dagger.DaggerWithdrawDetailsComponent;
import anda.travel.driver.module.main.mine.walletnew.withdrawal.withdrawalrecord.withdrawldetails.dagger.WithdrawDetailsModule;
import anda.travel.driver.module.vo.WithdrawDetailVO;
import anda.travel.driver.util.SysConfigUtils;
import anda.travel.driver.util.TimeUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class WithdrawDetailsActivity extends BaseActivity implements WithdrawDetailsContract.View {
    @BindView(R2.id.tv_withdraw_fee)
    TextView tv_withdraw_fee;
    @BindView(R2.id.tv_withdraw_way)
    TextView tv_withdraw_way;
    @BindView(R2.id.tv_withdraw_account_name)
    TextView tv_withdraw_account_name;
    @BindView(R2.id.tv_withdraw_account)
    TextView tv_withdraw_account;
    @BindView(R2.id.tv_poundage)
    TextView tv_poundage;
    @BindView(R2.id.tv_apply_time)
    TextView tv_apply_time;
    @BindView(R2.id.tv_audit_time)
    TextView tv_audit_time;
    @BindView(R2.id.tv_audit_result)
    TextView tv_audit_result;
    @BindView(R2.id.tv_remark)
    TextView tv_remark;

    @Inject
    WithdrawDetailsPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_withdraw_details);
        ButterKnife.bind(this);

        DaggerWithdrawDetailsComponent.builder()
                .appComponent(HxClientManager.getAppComponent())
                .withdrawDetailsModule(new WithdrawDetailsModule(this))
                .build().inject(this);
        String cashUuid = getIntent().getStringExtra("cashUuid");
        mPresenter.reqWithdrawalDetails(cashUuid);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void showWithdrawalDetails(WithdrawDetailVO vo) {
        tv_withdraw_fee.setText(getString(R.string.withdraw_fee, NumberUtil.getFormatValue(vo.withdrawalFee)));
        tv_poundage.setText(getString(R.string.yuan,
                NumberUtil.getFormatValue(vo.cashFee != null ? vo.cashFee : SysConfigUtils.get().getCashFee(),
                        true)));
        tv_withdraw_account_name.setText(vo.withdrawalAccountName);
        tv_withdraw_account.setText(vo.withdrawalAccount);
        tv_apply_time.setText(TimeUtils.paseDateAndTime(vo.applyTime));
        tv_audit_time.setText(vo.auditTime == null ? "无" : TimeUtils.paseDateAndTime(vo.auditTime));
        if (vo.auditResult == 1 || vo.auditResult == 10) {
            tv_audit_result.setText(getResources().getString(R.string.submit_applications));
        } else if (vo.auditResult == 2) {
            tv_audit_result.setText(getResources().getString(R.string.withdrawal_success));
        } else if (vo.auditResult == 3) {
            tv_audit_result.setText(getResources().getString(R.string.withdrawal_failure));
        } else if (vo.auditResult == 4 || vo.auditResult == 20) {
            tv_audit_result.setText(getResources().getString(R.string.withdrawal_deliver));
        } else if (vo.auditResult == 30) {
            tv_audit_result.setText("打款成功");
        } else if (vo.auditResult == 40) {
            tv_audit_result.setText("打款失败");
        } else if (vo.auditResult == 50) {
            tv_audit_result.setText("审核拒绝");
        } else if (vo.auditResult == 60) {
            tv_audit_result.setText("审核异常");
        }
        tv_remark.setText(TextUtils.isEmpty(vo.remark) ? getString(R.string.none) : vo.remark);
    }
}
