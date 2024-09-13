package anda.travel.driver.module.main.mine.carinfo.mile;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.data.user.UserRepository;

public class MilePresenter extends BasePresenter implements MileContract.Presenter {

    private final UserRepository mUserRepository;
    private final MileContract.View mView;

    @Inject
    public MilePresenter(UserRepository userRepository, MileContract.View view) {
        mUserRepository = userRepository;
        mView = view;
    }

    @Override
    public void reqMile(String startTime, String endTime) {
        mDisposable.add(mUserRepository
                .reqMile(startTime, endTime)
                .compose(RxUtil.applySchedulers())
                .doAfterTerminate(mView::onRefreshComplete)
                .subscribe(entity -> {
                    mView.noMore();
                    mView.setData(entity);
                }, ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    public void reqRefresh(String startTime, String endTime) {
        reqMile(startTime, endTime);
    }

    public void reqRefreshWithLoading(String startTime, String endTime) {
        mDisposable.add(mUserRepository
                .reqMile(startTime, endTime)
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingView(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(entity -> {
                    mView.noMore();
                    mView.setData(entity);
                }, ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }
}
