package anda.travel.driver.module.order.cancel;

import android.text.TextUtils;

import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.config.OrderStatus;
import anda.travel.driver.data.entity.CancelReasonEntity;
import anda.travel.driver.data.order.OrderRepository;
import anda.travel.driver.data.user.UserRepository;

/**
 * 功能描述：
 */
public class OrderCancelPresenter extends BasePresenter implements OrderCancelContract.Presenter {

    private final OrderCancelContract.View mView;
    private final OrderRepository mOrderRepository;
    private final UserRepository mUserRepository;
    private String mOrderUuid;
    private int mStatus = OrderStatus.WATI_PASSENGER_GET_ON;

    @Inject
    public OrderCancelPresenter(UserRepository userRepository, OrderCancelContract.View view, OrderRepository orderRepository) {
        mUserRepository = userRepository;
        mView = view;
        mOrderRepository = orderRepository;
    }

    public void onCreate() {
        List<CancelReasonEntity> list = mUserRepository.getCancelMsgList();
        if (list == null || list.isEmpty()) return;
        mView.showCancelMsg(list);
    }

    @Override
    public void subscribe() {
        super.subscribe();
        if (mFirstSubscribe) {
            reqCancelMsg(); //获取取消标签
        }
    }

    @Override
    public void setOrderUuid(String orderUuid, int status) {
        mOrderUuid = orderUuid;
        mStatus = status;
    }

    @Override
    public String getOrderUuid() {
        return mOrderUuid;
    }

    @Override
    public void reqCancelMsg() {
        mDisposable.add(mUserRepository.reqCancelMsg()
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingView(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(mView::showCancelMsg
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository))
        );
    }

    @Override
    public void submitCancel(String content) {
        if (TextUtils.isEmpty(content)) {
            mView.toast("请选择取消原因");
            return;
        }
        mDisposable.add(mOrderRepository.reqCancelOrder(mOrderUuid, mStatus, content)
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingView(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(s -> mView.cancelSuccess(mOrderUuid)
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }
}
