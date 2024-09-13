package anda.travel.driver.module.main.mine.walletnew.withdrawal;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.network.RequestError;
import anda.travel.driver.baselibrary.network.RequestParams;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.config.CodeType;
import anda.travel.driver.data.user.UserRepository;

public class WithdrawalPresenter extends BasePresenter implements WithdrawalContract.Presenter {

    WithdrawalContract.View mView;
    UserRepository mUserRepository;

    @Inject
    public WithdrawalPresenter(UserRepository userRepository, WithdrawalContract.View view) {
        mUserRepository = userRepository;
        mView = view;
    }

    @Override
    public void sendCode() {
        mDisposable.add(mUserRepository.sendCode(mUserRepository.getAccount(), CodeType.WITHDRAWAL.getType())
                .compose(RxUtil.applySchedulers())
                .subscribe(it -> mView.sendCodeSuccess()
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    @Override
    public void findBindAliAccount() {
        mDisposable.add(mUserRepository.findBindAliAccount()
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingViewWithDelay(false))
                .doAfterTerminate(() -> mView.hideLoadingView())
                .subscribe(bindAliAccount -> mView.showBingAliAccount(bindAliAccount)
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    @Override
    public void findAliAccountBySuccess() {
        mDisposable.add(mUserRepository.findAliAccountBySuccess()
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingViewWithDelay(false))
                .doAfterTerminate(() -> mView.hideLoadingView())
                .subscribe(aliAccountEntity -> mView.showAliAccountBySuccess(aliAccountEntity)
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    @Override
    public void withdrawalOneSelf(String driverAccountUuid, String identifyCode, String collectAccount, int collectType) {
        RequestParams params = new RequestParams.Builder()
                .putParam("driverAccountUuid", driverAccountUuid)
                .putParam("identifyCode", identifyCode)
                .putParam("collectType", collectType)
                .build();

        mDisposable.add(mUserRepository.withdrawal(params)
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingView(false))
                .doAfterTerminate(() -> mView.hideLoadingView())
                .subscribe(driverEntity -> mView.withdrawalSucc(collectAccount)
                        , ex -> {
                            showNetworkError(ex, R.string.network_error, mView, mUserRepository);
                            if (ex instanceof RequestError) {
                                RequestError error = ((RequestError) ex);
                                mView.withdrawalFail(error.getReturnCode(), error.getMessage());
                            }
                        }));
    }

    @Override
    public void withdrawalOthers(String driverAccountUuid, String identifyCode, String collectName, String collectAccount, int collectType) {
        RequestParams params = new RequestParams.Builder()
                .putParam("driverAccountUuid", driverAccountUuid)
                .putParam("identifyCode", identifyCode)
                .putParam("collectName", collectName)
                .putParam("collectAccount", collectAccount)
                .putParam("collectType", collectType)
                .build();

        mDisposable.add(mUserRepository.withdrawal(params)
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingView(false))
                .doAfterTerminate(() -> mView.hideLoadingView())
                .subscribe(driverEntity -> mView.withdrawalSucc(collectAccount)
                        , ex -> {
                            showNetworkError(ex, R.string.network_error, mView, mUserRepository);
                            if (ex instanceof RequestError) {
                                RequestError error = ((RequestError) ex);
                                mView.withdrawalFail(error.getReturnCode(), error.getMessage());
                            }
                        }));
    }

}
