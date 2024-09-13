package anda.travel.driver.module.order.pay;

import android.content.res.Resources;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.data.entity.PayTypeEntity;
import anda.travel.driver.data.order.OrderRepository;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.event.OrderEvent;
import anda.travel.driver.event.PayEvent;

/**
 * 功能描述：
 */
public class OrderPayPresenter extends BasePresenter implements OrderPayContract.Presenter {

    private final OrderPayContract.View mView;
    private final OrderRepository mOrderRepository;
    private final UserRepository mUserRepository;

    private String mOrderUuid; //订单编号

    @Inject
    public OrderPayPresenter(OrderPayContract.View view, OrderRepository orderRepository, UserRepository userRepository) {
        mView = view;
        mOrderRepository = orderRepository;
        mUserRepository = userRepository;
    }

    public void onCreate() {
        EventBus.getDefault().register(this);
        //先从本地获取支付方式列表
        List<PayTypeEntity> list = mUserRepository.getLocalPayTypeList();
        if (list == null || list.isEmpty()) {
            list = getDefaultList(mUserRepository.getIsDependDriver());
        }
        mView.showPayTypeList(list);
        //获取最新的支付方式列表
        reqPayTypeList();
    }

    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderEvent(OrderEvent event) {
        if (event.type == OrderEvent.PAY_SUCCESS) {
            mView.paySuccess();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPayEvent(PayEvent event) {
        switch (event.type) {
            case PayEvent.PAY_SUCCESS: //支付成功
                mView.paySuccess();
                break;
            case PayEvent.PAY_FAIL: //支付失败
                mView.payFail();
                break;
        }
    }

    @Override
    public boolean getIsDependDriver() {
        return mUserRepository.getIsDependDriver();
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
    public void reqPayByWeixin(String ip) {
        //执行"微信支付"
        mDisposable.add(mOrderRepository.payByWechat(mOrderUuid, ip)
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingView(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(mView::startWxpay
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    @Override
    public void reqPayByAlipay() {
        //执行"支付宝支付"
        mDisposable.add(mOrderRepository.payByAlipay(mOrderUuid)
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingView(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(mView::startAlipay
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    @Override
    public void reqPayByBalance() {
        mDisposable.add(mOrderRepository.payByBalance(mOrderUuid)
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingView(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(s -> mView.paySuccess()
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    @Override
    public void reqPayTypeList() {
        mDisposable.add(mUserRepository.getPayTypeList()
                .compose(RxUtil.applySchedulers())
                .subscribe(list -> {
                    if (list == null || list.isEmpty()) {
                        list = getDefaultList(mUserRepository.getIsDependDriver());
                    }
                    mView.showPayTypeList(list);
                }, ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    /**
     * 获取默认的支付方式
     *
     * @return
     */
    private List<PayTypeEntity> getDefaultList(boolean isDepend) {
        Resources res = mView.getContext().getResources();
        List<PayTypeEntity> list = new ArrayList<>();
        if (!isDepend) list.add(new PayTypeEntity(res.getString(R.string.order_pay_balance)));
        list.add(new PayTypeEntity(res.getString(R.string.order_pay_alipay)));
        return list;
    }
}
