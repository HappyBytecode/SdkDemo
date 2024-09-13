package anda.travel.driver.module.main.mine.walletnew.withdrawal;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.text.MessageFormat;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.baselibrary.utils.PhoneUtil;
import anda.travel.driver.baselibrary.utils.RegUtil;
import anda.travel.driver.baselibrary.view.HeadView;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.data.entity.AliAccountEntity;
import anda.travel.driver.module.main.mine.walletnew.withdrawal.dagger.DaggerWithdrawalComponent;
import anda.travel.driver.module.main.mine.walletnew.withdrawal.dagger.WithdrawalModule;
import anda.travel.driver.util.Navigate;
import anda.travel.driver.util.SysConfigUtils;
import anda.travel.driver.widget.CodeInputDialog;
import anda.travel.driver.widget.EnhanceTabLayout;
import anda.travel.driver.widget.MyWalletCommonDialog;
import anda.travel.driver.widget.codeinput.VerificationCodeInputView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 提现
 */
@SuppressLint("NonConstantResourceId")
public class WithdrawalActivity extends BaseActivity implements WithdrawalContract.View {

    @BindView(R2.id.head_view)
    HeadView head_view;
    @BindView(R2.id.tv_withdrawal)
    TextView tv_withdrawal;
    @BindView(R2.id.tv_notice)
    TextView mTvNotice;
    @BindView(R2.id.tab)
    EnhanceTabLayout tabLayout;
    @BindView(R2.id.ll_withdraw_own)
    LinearLayout ll_withdraw_own;
    @BindView(R2.id.tv_click_bind)
    TextView tv_click_bind;
    //提现到他人支付宝tab相关控件
    @BindView(R2.id.ll_withdraw_others)
    LinearLayout ll_withdraw_others;
    @BindView(R2.id.tv_withdraw_amount_others)
    TextView tv_withdraw_amount_others;
    @BindView(R2.id.et_account_name)
    EditText et_account_name;
    @BindView(R2.id.et_zfb_account)
    EditText et_zfb_account;
    @BindView(R2.id.tv_withdraw_amount)
    TextView tv_withdraw_amount;

    //提现方式
    int collectType = 1;

    @Inject
    WithdrawalPresenter mPresenter;
    private boolean isBindOneself = true;//是否提现到本人支付宝tab ,默认true

    private String bindAliAccount;

    private String driverAccountUuid;//司机账户编号

    private boolean isClickResend;
    private CodeInputDialog codeInputDialog;

    //提现到他人
    private String zfbAccountName;
    private String zfbAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_withdrawal_new);
        ButterKnife.bind(this);
        DaggerWithdrawalComponent.builder()
                .appComponent(HxClientManager.getAppComponent())
                .withdrawalModule(new WithdrawalModule(this))
                .build().inject(this);
        double cashFee = SysConfigUtils.get().getCashFee();
        String balance = getIntent().getStringExtra("balance");
        driverAccountUuid = getIntent().getStringExtra("driverAccountUuid");
        tv_withdraw_amount.setText(MessageFormat.format("{0}元", balance));
        tv_withdraw_amount_others.setText(MessageFormat.format("{0}元", balance));
        mTvNotice.setText(getResources().getString(R.string.to_ensure_the_safety_of_funds_own,
                "" + cashFee));
        tabLayout.addTab("提现到本人支付宝");
        tabLayout.addTab("提现到他人支付宝");
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    isBindOneself = true;
                    ll_withdraw_own.setVisibility(View.VISIBLE);
                    ll_withdraw_others.setVisibility(View.GONE);
                    mPresenter.findBindAliAccount();
                } else if (tab.getPosition() == 1) {
                    isBindOneself = false;
                    ll_withdraw_own.setVisibility(View.GONE);
                    ll_withdraw_others.setVisibility(View.VISIBLE);
                    mPresenter.findAliAccountBySuccess();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        et_account_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String strs = et_account_name.getText().toString().trim();
                String str = RegUtil.filterChinese(strs);
                if (!strs.equals(str)) {
                    et_account_name.setText(str);
                    et_account_name.setSelection(str.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onResume() {
        if (isBindOneself) {
            mPresenter.findBindAliAccount();
        } else {
            mPresenter.findAliAccountBySuccess();
        }
        super.onResume();
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R2.id.tv_withdrawal, R2.id.tv_click_bind})
    public void onClick(View view) {
        if (isBtnBuffering()) {
            return;
        }
        int id = view.getId();
        if (id == R.id.tv_withdrawal) {
            if (isBindOneself) {
                withdrawalOneself();
            } else {
                withdrawalOthers();
            }
        } else if (id == R.id.tv_click_bind) {
            if (!TextUtils.isEmpty(bindAliAccount)) {
                Navigate.openBindAliPay(this, bindAliAccount);
            } else {
                Navigate.openBindAliPay(this);
            }
        }
    }

    /**
     * 提现到他人支付宝
     */
    public void withdrawalOthers() {
        zfbAccountName = et_account_name.getText().toString().trim();
        if (TextUtils.isEmpty(zfbAccountName)) {
            toast(getResources().getString(R.string.please_input_account_name));
            return;
        }
        if (!RegUtil.isName(zfbAccountName)) {
            toast("请输入正确的支付宝账户姓名");
            return;
        }
        zfbAccount = et_zfb_account.getText().toString().trim();
        if (TextUtils.isEmpty(zfbAccount)) {
            toast(getResources().getString(R.string.please_input_card_number));
            return;
        }
        if (!PhoneUtil.isMobileNO(zfbAccount) && !RegUtil.isEmail(zfbAccount)) {
            toast(getResources().getString(R.string.input_card_number_error));
            return;
        }
        collectType = 1;
        if (!TextUtils.isEmpty(driverAccountUuid)) {
            isClickResend = false;
            mPresenter.sendCode();
        }
    }

    /**
     * 提现到本人支付宝
     */
    public void withdrawalOneself() {
        collectType = 1;

        if (TextUtils.isEmpty(bindAliAccount)) {
            toast("请先绑定支付宝");
            return;
        }
        if (!TextUtils.isEmpty(driverAccountUuid)) {
            isClickResend = false;
            mPresenter.sendCode();
        }
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void showBingAliAccount(String bindAliAccount) {
        if (!TextUtils.isEmpty(bindAliAccount)) {
            this.bindAliAccount = bindAliAccount;
            tv_click_bind.setText(bindAliAccount);
        }
    }

    @Override
    public void showAliAccountBySuccess(AliAccountEntity aliAccountEntity) {
        if (null != aliAccountEntity) {
            et_account_name.setText(aliAccountEntity.getPayeeName());
            et_zfb_account.setText(aliAccountEntity.getPayeeAccount());
        }
    }

    @Override
    public void sendCodeSuccess() {
        if (isClickResend) {
            if (null != codeInputDialog) {
                codeInputDialog.setTimerStart();
            }
        } else {
            codeInputDialog = new CodeInputDialog(this)
                    .setCloseListener(() -> {

                    }).setCodeListener(() -> {
                        isClickResend = true;
                        mPresenter.sendCode();
                    })
                    .setOnInputListener(new VerificationCodeInputView.OnInputListener() {
                        @Override
                        public void onComplete(String code) {
                            if (null != codeInputDialog) {
                                codeInputDialog.closeDialog();
                            }
                            if (isBindOneself) {
                                mPresenter.withdrawalOneSelf(driverAccountUuid, code, bindAliAccount, collectType);
                            } else {
                                mPresenter.withdrawalOthers(driverAccountUuid, code, zfbAccountName
                                        , zfbAccount, collectType);
                            }
                        }

                        @Override
                        public void onInput() {

                        }
                    });
            codeInputDialog.show();
        }
    }

    /**
     * 提现成功
     */
    @Override
    public void withdrawalSucc(String zfbAccount) {
        new MyWalletCommonDialog(this, () -> {
            Navigate.openWithdrawalRecord(this);
            finish();
        }).setTitle("提现申请成功")
                .setContent("本次提现金额将转入以下支付宝账户中：\n\n", zfbAccount)
                .setSubTitle("")
                .show();
    }

    /**
     * 提现失败
     *
     * @param errCode 错误码
     * @param errMsg  错误原因
     */
    @Override
    public void withdrawalFail(int errCode, String errMsg) {
        toast(errMsg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

}
