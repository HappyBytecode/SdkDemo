package anda.travel.driver.module.main.mine.walletnew.withdrawal.bindalipay;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.config.CodeType;
import anda.travel.driver.data.user.UserRepository;

public class BindAliPayPresenter extends BasePresenter implements BindAliPayContract.Presenter {
    private final BindAliPayContract.View mView;
    private final UserRepository mUserRepository;

    @Inject
    public BindAliPayPresenter(UserRepository userRepository, BindAliPayContract.View view) {
        this.mUserRepository = userRepository;
        this.mView = view;
    }

    @Override
    public void sendCode() {
        mDisposable.add(mUserRepository.sendCode(mUserRepository.getAccount(), CodeType.BIND_ALI_ACCOUNT.getType())
                .compose(RxUtil.applySchedulers())
                .subscribe(it -> mView.sendCodeSuccess()
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    @Override
    public void bindAliAccount(String zfbAccount, String identifyCode) {
        mDisposable.add(mUserRepository.bindAliAccount(zfbAccount, identifyCode)
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingView(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(it -> mView.showBindAliAccountSuccess(zfbAccount)
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }
}
