package anda.travel.driver.module.order.complain;

import android.text.TextUtils;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.baselibrary.utils.TypeUtil;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.data.entity.ComplainResultEntity;
import anda.travel.driver.data.order.OrderRepository;
import anda.travel.driver.data.user.UserRepository;

public class OrderComplainPresenter extends BasePresenter implements OrderComplainContract.Presenter {

    private final OrderComplainContract.View mView;
    private final OrderRepository mOrderRepository;
    private final UserRepository mUserRepository;
    private String mOrderUuid;

    @Inject
    public OrderComplainPresenter(OrderComplainContract.View view, OrderRepository orderRepository, UserRepository userRepository) {
        mView = view;
        mOrderRepository = orderRepository;
        mUserRepository = userRepository;
    }

    @Override
    public void subscribe() {
        super.subscribe();
        reqComplainStatus();
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
    public void reqComplainStatus() {
        mDisposable.add(mOrderRepository.isComplain(mOrderUuid)
                .compose(RxUtil.applySchedulers())
                .subscribe(data -> {
                    ComplainResultEntity entity = data.body();
                    if (entity == null) {
                        mView.complained(
                                "",
                                "",
                                "",
                                false, false);
                        return;
                    }
                    int status = TypeUtil.getValue(entity.status);
                    switch (status) {
                        case 1:
                        case 2:
                        case 3:
                            mView.complained(
                                    TypeUtil.getValue(entity.contents),
                                    TypeUtil.getValue(entity.remark),
                                    TypeUtil.getValue(entity.result),
                                    true, entity.status == 3); //投诉处理中
                            break;
                        default:
                            mView.complained(
                                    TypeUtil.getValue(entity.contents),
                                    TypeUtil.getValue(entity.remark),
                                    TypeUtil.getValue(entity.result),
                                    false, false); //投诉处理中
                            break;
                    }
                }, ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    @Override
    public void reqComplainMsg() {
        mDisposable.add(mUserRepository.reqComplainMsg()
                .compose(RxUtil.applySchedulers())
                .subscribe(mView::showComplainMsg
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository))
        );
    }

    @Override
    public void submitComplain(final String content, String remark) {
        if (TextUtils.isEmpty(content) && TextUtils.isEmpty(remark)) {
            mView.toast("请选择投诉原因");
            return;
        }
        mDisposable.add(mOrderRepository.reqComplainOrder(mOrderUuid, content, remark)
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingView(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(s -> mView.complainSuccess(content, remark)
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }
}
