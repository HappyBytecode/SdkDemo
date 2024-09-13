package anda.travel.driver.module.amap.heatmap;

import android.annotation.SuppressLint;

import javax.inject.Inject;

import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.data.order.OrderRepository;
import anda.travel.driver.data.user.UserRepository;

public class HeatMapPresenter extends BasePresenter implements HeatMapContract.Presenter {
    private final HeatMapContract.View mView;
    private final OrderRepository mOrderRepository;
    private final UserRepository mUserRepository;

    @Inject
    public HeatMapPresenter(HeatMapContract.View view, OrderRepository orderRepository, UserRepository userRepository) {
        mView = view;
        mUserRepository = userRepository;
        mOrderRepository = orderRepository;
    }

    @Override
    public void getZoomType(String versionNo, String moduleUuid) {
        mDisposable.add(mUserRepository.reqActHtmlVersion(versionNo, moduleUuid)
                .compose(RxUtil.applySchedulers())
                .subscribe(
                        vo -> {
                            String type = vo.getUpdNote();
                            mView.setCanZoom("可缩放".equals(type));
                        }, ex -> mView.setCanZoom(false)
                )
        );
    }

    @SuppressLint("CheckResult")
    @Override
    public void getPoints(int timeType) {
        mDisposable.add(mOrderRepository.findOrderHeatMap(timeType)
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingViewWithDelay(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(data -> {
                            if (data != null && data.size() > 0) {
                                mView.showHeatMap(data);
                            } else {
                                mView.toastFail();
                            }
                        }, ex -> mView.toastFail()
                ));
    }
}
