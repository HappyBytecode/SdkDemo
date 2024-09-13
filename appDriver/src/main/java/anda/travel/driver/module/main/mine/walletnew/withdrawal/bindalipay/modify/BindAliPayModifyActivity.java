package anda.travel.driver.module.main.mine.walletnew.withdrawal.bindalipay.modify;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.baselibrary.utils.PhoneUtil;
import anda.travel.driver.baselibrary.utils.RegUtil;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.databinding.HxycActivityBindAliPayModifyBinding;
import anda.travel.driver.event.UIEvent;
import anda.travel.driver.module.main.mine.walletnew.withdrawal.bindalipay.modify.dagger.BindAliPayModifyModule;
import anda.travel.driver.module.main.mine.walletnew.withdrawal.bindalipay.modify.dagger.DaggerBindAliPayModifyComponent;
import anda.travel.driver.widget.CodeInputDialog;
import anda.travel.driver.widget.MyWalletCommonDialog;
import anda.travel.driver.widget.codeinput.VerificationCodeInputView;

public class BindAliPayModifyActivity extends BaseActivity implements BindAliPayModifyContract.View {
    @Inject
    BindAliPayModifyPresenter mPresenter;

    private HxycActivityBindAliPayModifyBinding binding;
    private boolean isClickResend;
    private CodeInputDialog codeInputDialog;
    private String zfbAccountNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = HxycActivityBindAliPayModifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DaggerBindAliPayModifyComponent.builder()
                .appComponent(HxClientManager.getAppComponent())
                .bindAliPayModifyModule(new BindAliPayModifyModule(this))
                .build().inject(this);
        initView();
    }

    private void initView() {
        String bindAilAccount = getIntent().getStringExtra(IConstants.BIND_ZFB_ACCOUNT);
        binding.tvBindZfbAccount.setText(bindAilAccount);
        setHint("请输入新的支付宝账号(手机号/邮箱)", binding.etZfbAccountNew);
        new MyWalletCommonDialog(this, () -> {

        }).setTitle("支付宝绑定提示").setSubTitle("")
                .setContent("1、请注意核对账号正确性，并确认账号已完成实名认证。\n" +
                        "\n" +
                        "2、支付宝个人信息需与司机账号个人信息保持一致，否则无法提现。\n" +
                        "\n" +
                        "3、请确认账号已开通单笔转账功能，您的所有提现金额将统一转入该账户。\n")
                .show();
    }

    private void setHint(String str, EditText editText) {
        SpannableString s = new SpannableString(str);
        AbsoluteSizeSpan textSize = new AbsoluteSizeSpan(12, true);
        s.setSpan(textSize, 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setHint(s);
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

    public void onClick(View view) {
        if (isBtnBuffering()) {
            return;
        }
        if (view.getId() == R.id.tv_bind) {
            zfbAccountNew = binding.etZfbAccountNew.getText().toString().trim();
            String zfbAccount = binding.tvBindZfbAccount.getText().toString().trim();
            if (TextUtils.isEmpty(zfbAccountNew)) {
                toast("请先输入新的支付宝账号");
                return;
            }
            if (!PhoneUtil.isMobileNO(zfbAccountNew) && !RegUtil.isEmail(zfbAccountNew)) {
                toast(getResources().getString(R.string.input_card_number_error));
                return;
            }
            if (zfbAccountNew.equals(zfbAccount)) {
                toast("新支付宝账号不能与绑定支付宝账号相同");
                return;
            }
            isClickResend = false;
            mPresenter.sendCode();
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
                            mPresenter.bindAliAccount(zfbAccountNew, code);
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
        new MyWalletCommonDialog(this, () -> {
            EventBus.getDefault().post(new UIEvent(UIEvent.CLOSE_BIND_ALI_ACCOUNT_ACTIVITY));
            finish();
        }).setTitle("修改绑定成功")
                .setContent("新绑定的支付宝账户为:\n\n", zfbAccount)
                .setSubTitle("")
                .show();
    }
}
