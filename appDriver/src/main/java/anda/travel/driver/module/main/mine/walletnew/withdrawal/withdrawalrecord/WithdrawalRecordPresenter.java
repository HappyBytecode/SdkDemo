package anda.travel.driver.module.main.mine.walletnew.withdrawal.withdrawalrecord;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.module.vo.WithdrawaleRecordVO;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class WithdrawalRecordPresenter extends BasePresenter implements WithdrawalRecordContract.Presenter {
    private final WithdrawalRecordContract.View mView;
    private final UserRepository mUserRepository;

    @Inject
    public WithdrawalRecordPresenter(UserRepository userRepository, WithdrawalRecordContract.View view) {
        this.mUserRepository = userRepository;
        this.mView = view;
    }

    @Override
    public void reqWithdrawalRecord(int nowPage) {
        mDisposable.add(mUserRepository.widthdrawalRecord(nowPage)
                .flatMapIterable(entities -> entities)
                .map(WithdrawaleRecordVO::createFrom)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> mView.showLoadingViewWithDelay(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(mView::showWithdrawalRecord
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }
}
