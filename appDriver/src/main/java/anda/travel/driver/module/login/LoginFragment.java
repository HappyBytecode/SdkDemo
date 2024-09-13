package anda.travel.driver.module.login;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.utils.SpannableWrap;
import anda.travel.driver.baselibrary.view.HeadViewWhite;
import anda.travel.driver.common.BaseFragment;
import anda.travel.driver.configurl.MyConfig;
import anda.travel.driver.configurl.ParseUtils;
import anda.travel.driver.module.car.SelectCarActivity;
import anda.travel.driver.module.login.dagger.DaggerLoginComponent;
import anda.travel.driver.module.login.dagger.LoginModule;
import anda.travel.driver.module.web.WebActivity;
import anda.travel.driver.util.Navigate;
import anda.travel.driver.util.SysConfigUtils;
import anda.travel.driver.widget.CommonAlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class LoginFragment extends BaseFragment implements LoginContract.View {

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @BindView(R2.id.et_login_phone)
    EditText mEtLoginPhone;
    @BindView(R2.id.et_login_pwd)
    EditText mEtLoginPwd;
    @BindView(R2.id.iv_switch)
    ImageView mIvSwitch;
    @BindView(R2.id.btn_login)
    AppCompatButton mBtnLogin;
    @BindView(R2.id.iv_clean)
    ImageView mIvClean;
    @BindView(R2.id.iv_psw_clean)
    ImageView mIvPswClean;
    @BindView(R2.id.head_view)
    HeadViewWhite mHeadView;
    @BindView(R2.id.layout_other_way)
    ViewGroup mLayoutOtherWay;
    @BindView(R2.id.btn_one_key_login)
    MaterialButton mOneKeyOrder;
    @BindView(R2.id.tv_terms)
    TextView tvTerms;
    @BindView(R2.id.login_checkbox)
    AppCompatCheckBox loginCheckbox;

    @Inject
    LoginPresenter mPresenter;
    private DownloadManager manager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.hxyc_fragment_login, container, false);
        ButterKnife.bind(this, mView);

        if (!TextUtils.isEmpty(mPresenter.getAccount())) {
            mEtLoginPhone.setText(mPresenter.getAccount());
            visible(mIvClean);//初始化账户的时候，需要显示clear按钮
            Selection.setSelection(mEtLoginPhone.getEditableText(), mEtLoginPhone.getEditableText().length());
        }
        initView();
        mEtLoginPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String data = s.toString().trim();
                if (!TextUtils.isEmpty(data)) {
                    visible(mIvClean);
                } else {
                    gone(mIvClean);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 11) {
                    mEtLoginPwd.requestFocus(); //焦点切换到下一个输入框
                }
            }
        });
        mEtLoginPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String data = s.toString();
                if (!TextUtils.isEmpty(data)) {
                    visible(mIvSwitch, mIvPswClean);
                } else {
                    gone(mIvSwitch, mIvPswClean);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mPresenter.onCreate();

        return mView;
    }

    private void initView() {
        SpannableWrap.setText("我已阅读并同意").append(getString(R.string.login_terms))
                .onclick(v -> {
                    if (isBtnBuffering()) return;
                    MyConfig config = ParseUtils.getInstance().getMyConfig();
                    if (config == null || TextUtils.isEmpty(config.getUserAgreement())) {
                        toast("未获取到服务协议链接");
                        return;
                    }
                    WebActivity.actionStart(getContext(),
                            config.getUserAgreement(),
                            getResources().getString(R.string.login_terms_no_border));
                }, false)
                .textColor(ContextCompat.getColor(mView.getContext(), R.color.popup_item_choose))
                .append(getString(R.string.login_privacy))
                .onclick(v -> {
                    MyConfig config = ParseUtils.getInstance().getMyConfig();
                    if (config == null || TextUtils.isEmpty(config.getDriverPrivacyProtocol())) {
                        toast("未获取到服务协议链接");
                        return;
                    }
                    WebActivity.actionStart(getContext(),
                            config.getDriverPrivacyProtocol(),
                            getResources().getString(R.string.login_privacy_title));
                }, false)
                .textColor(ContextCompat.getColor(mView.getContext(), R.color.popup_item_choose))
                .into(tvTerms);
        loginCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> mBtnLogin.setEnabled(isChecked));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        DaggerLoginComponent.builder()
                .appComponent(getAppComponent())
                .loginModule(new LoginModule(this))
                .build().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.onDestroy();
    }

    @OnClick({R2.id.tv_login_forget,
            R2.id.btn_login, R2.id.iv_switch,
            R2.id.iv_clean, R2.id.btn_one_key_login, R2.id.iv_psw_clean})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_login) {
            if (isBtnBuffering()) return; //避免快速点击
            reqLogin();
        } else if (id == R.id.iv_switch) {
            boolean showPwd = !mIvSwitch.isSelected();
            mIvSwitch.setSelected(showPwd);
            changPwdDisplay(showPwd);
        } else if (id == R.id.iv_clean) {
            mEtLoginPhone.setText("");
        } else if (id == R.id.iv_psw_clean) {
            mEtLoginPwd.setText("");
        }
    }

    @Override
    public void loginIsFirst(String phone) {

    }

    @Override
    public void loginSuccess(boolean isSubstitute) {
        if (isSubstitute) {
            SelectCarActivity.actionStart(getContext(), SelectCarActivity.TYPE_LOGIN);
        } else {
            Navigate.openMain(getContext(), SelectCarActivity.TYPE_LOGIN);
        }
        requireActivity().finish();
    }

    @Override
    public void loginFail(int errCode, String errMsg) {
        toast(errMsg);
    }

    @Override
    public void changPwdDisplay(boolean showPwd) {
        if (showPwd) {
            mEtLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            mEtLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        Selection.setSelection(mEtLoginPwd.getEditableText(), mEtLoginPwd.getEditableText().length());
    }

    @Override
    public void clearPwd() { //清除密码
        mEtLoginPwd.getEditableText().clear();
    }

    @Override
    public void showAccountUnavailable(String reason) {
        if (TextUtils.isEmpty(reason)) reason = "您的账号已被封";
        new CommonAlertDialog(requireContext()).builder()
                .setTitle(reason)
                .setNegativeButton("我知道了", v -> {

                })
                .setPositiveButton("联系客服", v ->
                        SysConfigUtils.get().dialServerPhone(getContext()))
                .show();
    }

    @Override
    public void closeActivity() {
        requireActivity().finish();
    }

    private void reqLogin() {
        mPresenter.reqLogin(mEtLoginPhone.getEditableText().toString().trim(),
                mEtLoginPwd.getEditableText().toString().trim()
        );
    }
}
