package anda.travel.driver.module.order.detail;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.network.RequestError;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.baselibrary.utils.TypeUtil;
import anda.travel.driver.common.AppConfig;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.config.PumpingType;
import anda.travel.driver.config.WarnType;
import anda.travel.driver.data.entity.WarningContentEntity;
import anda.travel.driver.data.order.OrderRepository;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.event.MessageEvent;
import anda.travel.driver.event.OrderEvent;
import anda.travel.driver.event.PayEvent;
import anda.travel.driver.module.vo.OrderVO;
import anda.travel.driver.socket.SocketPushContent;
import anda.travel.driver.util.SpeechUtil;
import rx.schedulers.Schedulers;

/**
 * 功能描述：
 */
public class OrderDetailPresenter extends BasePresenter implements OrderDetailContract.Presenter {

    private final OrderDetailContract.View mView;
    private final OrderRepository mOrderRepository;
    private final UserRepository mUserRepository;

    private OrderVO mOrderVO; //订单信息
    private String mOrderUuid = ""; //当前订单编号
    private boolean mIsFront;

    @Inject
    public OrderDetailPresenter(OrderDetailContract.View view, OrderRepository orderRepository, UserRepository userRepository) {
        mView = view;
        mOrderRepository = orderRepository;
        mUserRepository = userRepository;
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
    public String getBusinessUuid() {
        if (mOrderVO == null) return "";
        return mOrderVO.getBusiUuid();
    }

    @Override
    public void setIntentOrderVO(OrderVO vo) {
        mOrderVO = vo;
        mView.setOrderInfo(vo);
    }

    @Override
    public OrderVO getOrderVO() {
        return mOrderVO;
    }

    @Override
    public void setOrderRefresh() {
        //将mFirstSubscribe设置为false，以便首次获取订单详情时，优先从服务端获取
        mFirstSubscribe = false;
    }

    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void subscribe() {
        super.subscribe();
        mIsFront = true;
        reqOrderDetail();
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
        mIsFront = false;
    }

    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderEvent(OrderEvent event) {
        switch (event.type) {
            case OrderEvent.TAXI_UPDATE_FARE:
                if (event.obj1 == null || event.obj2 == null) return;
                String orderUuid = (String) event.obj1; //获取订单编号
                double totalFare = (double) event.obj2; //获取保存的金额
                if (!orderUuid.equals(mOrderUuid)) return; //确认是否是当前订单，如果不是，不执行以下操作

                mView.showTotalFare(totalFare); //刷新显示
                if (mOrderVO != null) mOrderVO.drvTotalFare = totalFare; //缓存数据
                setOrderRefresh();
                break;
            case OrderEvent.ORDER_PASSENGER_ORDER_PAYED: //支付订单
            case OrderEvent.ORDER_PASSENGER_CANCEL: //取消订单
            case OrderEvent.PRICE_CHANGE: //金额改变
                if (event.obj1 == null) return;
                SocketPushContent push = (SocketPushContent) event.obj1;
                if (!push.data.orderUuid.equals(mOrderUuid)) return;
                reqOrderDetail(); //刷新订单
                break;
            case OrderEvent.ORDER_CHANGE_ADDRESS:
                reqOrderDetail();
                String address = (String) event.obj1;
                String speak = mView.getMyContext().getString(R.string.change_address_normal, address);
                SpeechUtil.speech(speak);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPayEvent(PayEvent event) {
        if (event.type == PayEvent.PAY_SUCCESS) {
            if (mIsFront) reqOrderDetail(); //刷新订单详情
        }
    }

    @Override
    public void reqOrderDetail() {
        mDisposable.add(mOrderRepository.reqOrderDetail(mOrderUuid)
                .map(OrderVO::createFrom)
                .compose(RxUtil.applySchedulers())
                .subscribe(vo -> {
                    mOrderVO = vo; //记录订单信息
                    mView.setOrderInfo(vo);
                }, ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    @Override
    public void rushFare() {
        mDisposable.add(mOrderRepository.rushFare(mOrderUuid)
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingView(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(s -> mView.toast(R.string.order_rush)
                        , ex -> {
                            if (ex instanceof RequestError) {
                                RequestError error = ((RequestError) ex);
                                if (error.getMessage() != null) {
                                    mView.toast(error.getMessage());
                                }
                            }
                        }));
    }

    @Override
    public void completeOrder(final int isPumping) {
        mDisposable.add(mOrderRepository.completeOrder(mOrderUuid)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSubscribe(() -> mView.showLoadingView(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(s -> reqOrderDetail()
                        , ex -> {
                            showNetworkError(ex, R.string.network_error, mView, mUserRepository);
                            dealwithStatusError(ex, isPumping); //订单状态错误时，刷新订单详情
                        }));
    }

    @Override
    public void dealwithStatusError(Throwable ex, Integer isPumping) {
        if (ex instanceof RequestError) {
            if (mOrderVO != null && isPumping != null
                    && isPumping == PumpingType.COMPLETE_WITH_PUMP
                    && ((RequestError) ex).getReturnCode() == 6001) {
                // 司机可用余额不足以支付抽成，则返回错误 6001
                mView.skipToPayPumping(mOrderVO.uuid, TypeUtil.getValue(mOrderVO.pumpinFare));
                return;
            }

            String errorMsg = ((RequestError) ex).getMsg();
            /* 订单状态错误时，刷新订单详情 */
            if (AppConfig.ORDER_STATUS_ERROR.equals(errorMsg)) reqOrderDetail();
        }
    }

    @Override
    public void warnCallback(String type, String warnUuid) {
        mDisposable.add(mUserRepository.warnCallback(type, warnUuid)
                .compose(RxUtil.applySchedulers())
                .subscribe(entity -> {
                }, ex -> {
                }));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.type == MessageEvent.SYS_WARNING) {
            if (event.obj1 == null) return;
            if (event.obj1 instanceof String) {
                WarningContentEntity entity = JSON.parseObject((String) event.obj1, WarningContentEntity.class);
                warnCallback(WarnType.RECIEVED, entity.getWarnUuid());
                mView.showWarningInfo(entity);
            }
        }
    }

    @Override
    public void reqFareItems() {
        mDisposable.add(mOrderRepository.reqFareItems(mOrderUuid)
                .compose(RxUtil.applySchedulers())
                .subscribe(mView::setDisplay
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }
}
