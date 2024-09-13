package anda.travel.driver.module.main.mine.walletnew;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.data.user.UserRepository;

public class MyWalletNewPresenter extends BasePresenter implements MyWalletNewContract.Presenter {

    private final MyWalletNewContract.View mView;
    private final UserRepository mUserRepository;

    @Inject
    public MyWalletNewPresenter(UserRepository userRepository, MyWalletNewContract.View view) {
        mUserRepository = userRepository;
        mView = view;
    }

    @Override
    public void subscribe() {
        super.subscribe();
    }

    @Override
    public void reqMyWallet() {
        mDisposable.add(mUserRepository.getMyWallet()
                .compose(RxUtil.applySchedulers())
                .doAfterTerminate(mView::refreshComplete)
                .subscribe(mView::showMyWallet
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    @Override
    public void reqOwnCashSetting() {
        mDisposable.add(mUserRepository.getOwnCashSetting()
                .compose(RxUtil.applySchedulers())
                .doAfterTerminate(mView::refreshComplete)
                .subscribe(entity -> {
                    if (null != entity) {
                        mView.showCashSetting(entity);
                    }
                }, ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    @Override
    public void reqRentBillInfo() {
        mDisposable.add(mUserRepository.getRentBillInfo()
                .compose(RxUtil.applySchedulers())
                .doAfterTerminate(mView::refreshComplete)
                .subscribe(mView::showRentBillInfo
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    @Override
    public void reqGetNotAvailableMoneyDesc() {
        mDisposable.add(mUserRepository.getNotAvailableMoneyDesc()
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingView(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(mView::showNotAvailableMoneyDesc
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository))
        );
    }

    @Override
    public void saveIsFirstMyWallet(boolean isFirstMyWallet) {
        mUserRepository.saveIsFirstMyWallet(isFirstMyWallet);
    }

    @Override
    public boolean getIsFirstMyWallet() {
        return mUserRepository.getIsFirstMyWallet();
    }

}
