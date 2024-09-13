package anda.travel.driver.module.main.mine.walletnew.withdrawal.withdrawalrecord.withdrawldetails;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.module.vo.WithdrawDetailVO;

public class WithdrawDetailsPresenter extends BasePresenter implements WithdrawDetailsContract.Presenter {
    private final UserRepository mUserRepository;
    private final WithdrawDetailsContract.View mView;

    @Inject
    public WithdrawDetailsPresenter(UserRepository userRepository, WithdrawDetailsContract.View view) {
        mUserRepository = userRepository;
        mView = view;
    }

    @Override
    public void reqWithdrawalDetails(String cashUuid) {
        mDisposable.add(mUserRepository.getWithdrawalInfo(cashUuid)
                .map(WithdrawDetailVO::createFrom)
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingViewWithDelay(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(mView::showWithdrawalDetails
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    @Override
    public void reqOwnWithdrawalDetails(String cashUuid) {
        mDisposable.add(mUserRepository.getOwnWithdrawalInfo(cashUuid)
                .map(WithdrawDetailVO::createFrom)
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingViewWithDelay(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(mView::showWithdrawalDetails
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }
}
