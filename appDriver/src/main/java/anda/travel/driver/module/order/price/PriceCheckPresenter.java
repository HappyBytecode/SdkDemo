package anda.travel.driver.module.order.price;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.data.duty.DutyRepository;
import anda.travel.driver.data.order.OrderRepository;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.event.OrderEvent;
import anda.travel.driver.module.vo.OrderVO;
import anda.travel.driver.socket.SocketPushContent;

public class PriceCheckPresenter extends BasePresenter implements PriceCheckContract.Presenter {

    private final PriceCheckContract.View mView;
    private final OrderRepository mOrderRepository;
    private final UserRepository mUserRepository;
    private final DutyRepository mDutyRepository;
    private String mOrderUuid;
    private boolean mIsFront;

    @Inject
    public PriceCheckPresenter(PriceCheckContract.View view, OrderRepository orderRepository, UserRepository userRepository,
                               DutyRepository dutyRepository) {
        mView = view;
        mOrderRepository = orderRepository;
        mUserRepository = userRepository;
        mDutyRepository = dutyRepository;
    }

    /**
     * 在onCreate时调用
     */
    public void onCreate() {
        EventBus.getDefault().register(this);
        mDutyRepository.priceCheckCreate(); //订单进行中
    }

    @Override
    public void subscribe() {
        super.subscribe();
        mIsFront = true;
        reqFareItems(); //获取费用明细
        reqOrderDetail(); //刷新订单状态
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
        mIsFront = false;
    }

    /**
     * 在OnDestory时调用
     */
    public void onDestory() {
        EventBus.getDefault().unregister(this);
        mDutyRepository.priceCheckDestory(); //非进行中
    }

    @Override
    public void setOrderUuid(String orderUuid) {
        mOrderUuid = orderUuid;
    }

    @Override
    public String getOrderUuid() {
        return mOrderUuid;
    }

    @Override
    public void reqFareItems() {
        //显示费用明细
        mDisposable.add(mOrderRepository.reqFareItems(mOrderUuid)
                .compose(RxUtil.applySchedulers())
                .subscribe(mView::setDisplay
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    @Override
    public void confirmFare(
            double highwayFare, //高速费
            double bridgeFare, //过桥费
            double parkingFare //停车费
    ) {
        HashMap<String, String> params = new HashMap<>();
        params.put("orderUuid", mOrderUuid);
        params.put("highwayFare", String.valueOf(highwayFare));
        params.put("bridgeFare", String.valueOf(bridgeFare));
        params.put("parkingFare", String.valueOf(parkingFare));
        //确认费用成功，跳转订单详情页
        mDisposable.add(mOrderRepository.confirmFare(params)
                .map(OrderVO::createFrom)
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingView(false))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(mView::confirmFareSuccess
                        , ex -> {
                            mView.resetDisplay(); //重置按键显示
                            showNetworkError(ex, R.string.network_error, mView, mUserRepository);
                            reqOrderDetail(); //刷新订单状态
                        }));
    }

    @Override
    public void reqOrderDetail() {
        mDisposable.add(mOrderRepository.reqOrderDetail(mOrderUuid)
                .map(OrderVO::createFrom)
                .compose(RxUtil.applySchedulers())
                .subscribe(vo -> {
                    mView.judgeOrderStatus(vo);
                    if (vo != null) {
                        /////////根据字段判断异常订单提示
                        if (vo.abnormalStatus != null) {
                            switch (vo.abnormalStatus) {
                                case 1:
                                    mView.showCrossCityView();
                                    break;
                                case 2:
                                    mView.showAbnormalView();
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }, ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderEvent(OrderEvent event) {
        switch (event.type) {
            case OrderEvent.ORDER_PASSENGER_CANCEL: //取消订单
                if (event.obj1 == null) return;
                SocketPushContent push = (SocketPushContent) event.obj1;
                if (!push.data.orderUuid.equals(mOrderUuid)) return;
                if (mIsFront) reqOrderDetail(); //处于前端，才刷新状态
                break;
            case OrderEvent.PRICE_CHANGE: //金额改变
                reqFareItems(); //获取费用明细
                break;
        }
    }
}
