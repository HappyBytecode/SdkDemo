package anda.travel.driver.module.order.list;

import static anda.travel.driver.config.OrderStatus.ORDER_MAIN_STATUS_DOING;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.utils.NetworkUtil;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.config.DutyStatus;
import anda.travel.driver.config.OrderStatus;
import anda.travel.driver.data.duty.DutyRepository;
import anda.travel.driver.data.entity.SysConfigEntity;
import anda.travel.driver.data.order.OrderRepository;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.event.OrderEvent;
import anda.travel.driver.module.vo.OrderSummaryVO;
import anda.travel.driver.module.vo.OrderVO;
import anda.travel.driver.util.SysConfigUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 功能描述：
 */
public class OrderListPresenter extends BasePresenter implements OrderListContract.Presenter {

    private final OrderListContract.View mView;
    private final OrderRepository mOrderRepository;
    private final UserRepository mUserRepository;
    private final DutyRepository mDutyRepository;
    private int mPage = 1;

    //当前的订单类型，0:全部订单;10：行程创建（订单分配中）；20：行程进行中（待服务）；30：确认费用（待支付）；40：行程完结（已支付）；90：行程取消
    private int mCurType = OrderStatus.ROUTING_DEFAULT;

    private int mLoadType = LoadType.load_refresh;

    private static final class LoadType {
        public static final int load_refresh = 0;//加载类别，0：刷新加载
        public static final int load_more = 1;//加载类别，1：上拉加载
    }

    @Inject
    public OrderListPresenter(OrderListContract.View view, OrderRepository orderRepository, UserRepository userRepository, DutyRepository dutyRepository) {
        mView = view;
        mOrderRepository = orderRepository;
        mUserRepository = userRepository;
        mDutyRepository = dutyRepository;
    }

    @Override
    public void subscribe() {
        super.subscribe();
        reqRefresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderEvent(OrderEvent event) {
        switch (event.type) {
            case OrderEvent.ORDER_PASSENGER_ORDER_PAYED: //支付订单
            case OrderEvent.ORDER_PASSENGER_CANCEL: //取消订单
            case OrderEvent.PRICE_CHANGE: //金额改变
                if (event.obj1 == null) return;
                ///////刷新页面
                if (mPage > 1) {
                    reqMore();
                } else {
                    mView.hideNoMore();
                    reqRefresh();
                }
                break;
        }
    }

    /**
     * 刷新数据
     */
    public void reqRefresh() {
        mLoadType = LoadType.load_refresh;
        if (!NetworkUtil.isNetworkAvailable(HxClientManager.getInstance().application.getApplicationContext())) {
            mView.netErrorView(true);
            return;
        } else {
            mView.netErrorView(false);
        }
        HashMap<String, Integer> params = new HashMap<>();
        params.put("nowPage", 1);
        if (mCurType != OrderStatus.ROUTING_DEFAULT) {
            params.put("status", mCurType);
        }
        reqOrderList(params);
    }

    /**
     * 加载更多
     */
    public void reqMore() {
        mLoadType = LoadType.load_more;
        if (!NetworkUtil.isNetworkAvailable(HxClientManager.getInstance().application.getApplicationContext())) {
            mView.netErrorView(true);
            return;
        } else {
            mView.netErrorView(false);
        }
        HashMap<String, Integer> params = new HashMap<>();
        params.put("nowPage", mPage + 1);
        if (mCurType != OrderStatus.ROUTING_DEFAULT) {
            params.put("status", mCurType);
        }
        reqOrderList(params);
    }

    @Override
    public void reqOrderList(HashMap<String, Integer> params) {
        int page = params.get("nowPage");
        mDisposable.add(mOrderRepository.reqOrderList(params)
                .flatMapIterable(entities -> entities)
                .map(orderSummaryEntity -> {
                    SysConfigEntity sysConfigEntity = SysConfigUtils.get().getSysConfig();
                    if (null != sysConfigEntity) {
                        orderSummaryEntity.hintPaymentSwitch = sysConfigEntity.getHintPaymentSwitch();
                    }
                    return OrderSummaryVO.createFrom(orderSummaryEntity);
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> {
                    if (page <= 1) { //关闭下拉刷新图标
                        mView.onRefreshComplete();
                    }
                })
                .subscribe(vos -> {
                    if (vos.size() == 0 && page > 1) {
                        //说明此时加载的页面的上一页是最后一页，做toast提示
                        mView.noMore();
                        return;
                    }
                    mPage = page; //设置下标
                    if (mPage <= 1) { //刷新数据
                        mView.setData(vos);
                    } else { //添加数据
                        mView.addData(vos);
                    }
                }, ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    @Override
    public void reqOrderDetail(String orderUuid) {
        mDisposable.add(mOrderRepository.reqOrderDetail(orderUuid)
                .map(OrderVO::createFrom)
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingViewWithDelay(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(vo -> {
                    if (ORDER_MAIN_STATUS_DOING == vo.mainStatus
                            && mDutyRepository.getIsOnDuty() == DutyStatus.OFF_DUTY_INT) {
                        if (vo.canPickUp == null || vo.canPickUp != 1) {
                            mView.openOrderByStatus(vo);
                        } else {
                            mView.openOrderFailed(vo);
                        }
                    } else {
                        mView.openOrderByStatus(vo);
                    }
                }, ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    @Override
    public void setCurOrderType(int curType) {
        mPage = 1;
        mCurType = curType;
        mView.hideNoMore();
        reqRefresh();
    }

    @Override
    public void rushFare(String orderUuid) {
        mDisposable.add(mOrderRepository.rushFare(orderUuid)
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingView(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(s -> mView.toast(R.string.order_rush)
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    @Override
    public void reload() {
        //如果网络异常，点击重新加载时给出提示
        if (!NetworkUtil.isNetworkAvailable(HxClientManager.getInstance().application.getApplicationContext())) {
            mView.toast(R.string.network_error);
            return;
        }
        if (mLoadType == LoadType.load_refresh) {
            mView.hideNoMore();
            reqRefresh();
        } else {
            reqMore();
        }
    }

    public void onRegister() {
        EventBus.getDefault().register(this);
    }

    public void onRelease() {
        EventBus.getDefault().unregister(this);
    }
}
