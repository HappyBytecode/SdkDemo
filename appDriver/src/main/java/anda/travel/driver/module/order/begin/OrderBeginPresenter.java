package anda.travel.driver.module.order.begin;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.network.RequestError;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.common.AppConfig;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.data.dispatch.DispatchRepository;
import anda.travel.driver.data.entity.SysConfigEntity;
import anda.travel.driver.data.order.OrderRepository;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.event.OrderEvent;
import anda.travel.driver.module.vo.OrderVO;
import anda.travel.driver.socket.SocketPushContent;
import anda.travel.driver.util.SysConfigUtils;

public class OrderBeginPresenter extends BasePresenter implements OrderBeginContract.Presenter {

    private final OrderBeginContract.View mView;
    private final UserRepository mUserRepository;
    private final OrderRepository mOrderRepository;
    private final DispatchRepository mDispatchRepository;

    private String mOrderUuid;
    private OrderVO mVO;
    private SysConfigEntity mSysConfig;

    @Inject
    public OrderBeginPresenter(OrderBeginContract.View view,
                               UserRepository userRepository,
                               OrderRepository orderRepository,
                               DispatchRepository dispatchRepository) {
        mView = view;
        mUserRepository = userRepository;
        mOrderRepository = orderRepository;
        mDispatchRepository = dispatchRepository;
    }

    public void onCreate(String orderUuid, OrderVO vo) {
        mOrderUuid = orderUuid;
        mVO = vo;
        mView.setDisplay(vo);
        mSysConfig = SysConfigUtils.get().getSysConfig();
        if (mSysConfig != null) {
            if ("1".equals(mSysConfig.getImSwitch())) {

            }
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void subscribe() {
        super.subscribe();
        reqOrderDetail();
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
        if (mSysConfig != null) {
            if ("1".equals(mSysConfig.getImSwitch())) {

            }
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    public String getPassengerPhone() {
        return mVO == null ? "" : mVO.getPassengerPhone();
    }

    @Override
    public OrderVO getOrderVO() {
        return mVO;
    }

    @Override
    public void reqOrderDetail() {
        mDisposable.add(mOrderRepository.reqOrderDetail(mOrderUuid)
                .map(OrderVO::createFrom)
                .compose(RxUtil.applySchedulers())
                .subscribe(vo -> {
                    mVO = vo; //记录订单信息
                    mView.setDisplay(vo);
                }, ex -> {
                    showNetworkError(ex, R.string.network_error, mView, mUserRepository);
                }));
    }

    @Override
    public void reqOrderBegin() {
        // 出发去接乘客
        mDisposable.add(mOrderRepository.reqPickUpPas(mOrderUuid)
                .map(OrderVO::createFrom)
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingView(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(vo -> {
                    mDispatchRepository.dispatchComplete(mOrderUuid); // 先结束调度
                    mView.orderBeginSuccess(mOrderUuid, vo);
                }, ex -> {
                    mView.resetBtnDisplay(); //重置显示
                    showNetworkError(ex, R.string.network_error, mView, mUserRepository);
                    dealwithStatusError(ex); //订单状态错误时，刷新订单详情
                }));
    }

    @Override
    public void dealwithStatusError(Throwable ex) {
        if (ex instanceof RequestError) {
            String errorMsg = ((RequestError) ex).getMsg();
            /* 订单状态错误时，刷新订单详情 */
            if (AppConfig.ORDER_STATUS_ERROR.equals(errorMsg)) reqOrderDetail();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderEvent(OrderEvent event) {
        if (event.obj1 instanceof SocketPushContent) {
            SocketPushContent push = (SocketPushContent) event.obj1;
            if (!push.data.orderUuid.equals(mOrderUuid)) return;

            switch (event.type) {
                case OrderEvent.ORDER_PASSENGER_CANCEL: //取消订单
                    mView.closeView();
                    break;
                case OrderEvent.ORDER_DISTRIBUTE_TO_OTHER: //订单被改派:
                    mView.showOrderDistributToOther(mVO.getDistributeToOtherNotice());
                    break;
            }
        }
    }
}
