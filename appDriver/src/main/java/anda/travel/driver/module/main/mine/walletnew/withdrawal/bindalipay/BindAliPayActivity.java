package anda.travel.driver.module.main.mine.walletnew.withdrawal.bindalipay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.baselibrary.utils.PhoneUtil;
import anda.travel.driver.baselibrary.utils.RegUtil;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.databinding.HxycActivityBindAliPayBinding;
import anda.travel.driver.event.UIEvent;
import anda.travel.driver.module.main.mine.walletnew.withdrawal.bindalipay.dagger.BindAliPayModule;
import anda.travel.driver.module.main.mine.walletnew.withdrawal.bindalipay.dagger.DaggerBindAliPayComponent;
import anda.travel.driver.util.Navigate;
import anda.travel.driver.widget.CodeInputDialog;
import anda.travel.driver.widget.MyWalletCommonDialog;
import anda.travel.driver.widget.codeinput.VerificationCodeInputView;

/**
 * 提现绑定支付宝
 */
public class BindAliPayActivity extends BaseActivity implements BindAliPayContract.View {
    @Inject
    BindAliPayPresenter mPresenter;

    private String bindAilAccount;//绑定的阿里支付宝账号
    private HxycActivityBindAliPayBinding binding;

    private CodeInputDialog codeInputDialog;

    private boolean isClickResend;

    private String zfbAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = HxycActivityBindAliPayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        DaggerBindAliPayComponent.builder()
                .appComponent(HxClientManager.getAppComponent())
                .bindAliPayModule(new BindAliPayModule(this))
                .build().inject(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        Intent intent = getIntent();
        if (intent != null) {
            bindAilAccount = intent.getStringExtra(IConstants.BIND_ZFB_ACCOUNT);
        }
        initView();
    }

    private void initView() {
        if (!TextUtils.isEmpty(bindAilAccount)) {
            binding.rllModifyZfb.setVisibility(View.VISIBLE);
            binding.tvBindZfbAccount.setText(bindAilAccount);
            binding.rllBindZfb.setVisibility(View.GONE);
            binding.tvBind.setVisibility(View.GONE);
        } else {
            binding.rllModifyZfb.setVisibility(View.GONE);
            binding.rllBindZfb.setVisibility(View.VISIBLE);
            binding.tvBind.setVisibility(View.VISIBLE);
            setHint("请输入正确的支付宝账号(手机号/邮箱)", binding.etZfbName);
            new MyWalletCommonDialog(this, () -> {

            }).setTitle("支付宝绑定提示").setSubTitle("")
                    .setContent("1、请注意核对账号正确性，并确认账号已完成实名认证。\n" +
                            "\n" +
                            "2、支付宝个人信息需与司机账号个人信息保持一致，否则无法提现。\n" +
                            "\n" +
                            "3、请确认账号已开通单笔转账功能，您的所有提现金额将统一转入该账户。\n")
                    .show();
        }
    }

    private void setHint(String str, EditText editText) {
        SpannableString s = new SpannableString(str);
        AbsoluteSizeSpan textSize = new AbsoluteSizeSpan(12, true);
        s.setSpan(textSize, 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setHint(s);
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
                            mPresenter.bindAliAccount(zfbAccount, code);
                        }

                        @Override
                        public void onInput() {

                        }
                    });
            codeInputDialog.show();
        }
    }

    @Override
    public void showBindAliAccountSuccess(String zfbAccount) {
        new MyWalletCommonDialog(this, this::finish).setTitle("绑定成功")
                .setContent("当前绑定的支付宝账户为:\n\n", zfbAccount)
                .setSubTitle("")
                .show();
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
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        if (isBtnBuffering()) {
            return;
        }
        int id = view.getId();
        if (id == R.id.tv_bind) {
            zfbAccount = binding.etZfbName.getText().toString().trim();
            if (TextUtils.isEmpty(zfbAccount)) {
                toast(getResources().getString(R.string.please_input_card_number));
                return;
            }
            if (!PhoneUtil.isMobileNO(zfbAccount) && !RegUtil.isEmail(zfbAccount)) {
                toast(getResources().getString(R.string.input_card_number_error));
                return;
            }
            isClickResend = false;
            mPresenter.sendCode();
        } else if (id == R.id.tv_click_modify) {
            Navigate.openBindAliPayModify(this, bindAilAccount);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUIEvent(UIEvent event) {
        if (event.type == UIEvent.CLOSE_BIND_ALI_ACCOUNT_ACTIVITY) {
            finish(); //关闭界面
        }
    }
}
