package anda.travel.driver.module.main.mine.message;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.data.order.OrderRepository;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.event.MessageEvent;
import anda.travel.driver.module.vo.OrderVO;

public class MessagePresenter extends BasePresenter implements MessageContract.Presenter {

    private int mPage = 1;

    private final MessageContract.View mView;
    private final UserRepository mUserRepository;
    private final OrderRepository mOrderRepository;

    @Inject
    public MessagePresenter(MessageContract.View view,
                            UserRepository userRepository,
                            OrderRepository orderRepository) {
        mView = view;
        mUserRepository = userRepository;
        mOrderRepository = orderRepository;
    }

    @Override
    public void reqMessages(final int page) {
        mDisposable.add(mUserRepository
                .reqMessages(page)
                .compose(RxUtil.applySchedulers())
                .doAfterTerminate(() -> {
                    if (page <= 1) { //关闭下拉刷新图标
                        mView.onRefreshComplete();
                    }
                }).subscribe(messagesEntity -> {
                    if (messagesEntity.messages.size() == 0 && page > 1) {
                        //说明此时加载的页面的上一页是最后一页，做toast提示
                        mView.noMore();
                        return;
                    }
                    mPage = page; //设置下标
                    if (mPage <= 1) { //刷新数据
                        mView.setData(messagesEntity.messages);
                    } else { //添加数据
                        mView.addData(messagesEntity.messages);
                    }
                    postUnreadCount(messagesEntity.noReadCount);
                }, throwable -> showNetworkError(throwable, R.string.network_error, mView, mUserRepository)));
    }

    /**
     * 刷新未读总数
     *
     * @param noReadCount
     */
    private void postUnreadCount(int noReadCount) {
        EventBus.getDefault().post(new MessageEvent(MessageEvent.SYS_MSG, noReadCount));
    }

    @Override
    public void readMessage(String msgUuid) {
        mDisposable.add(mUserRepository.readMessage(msgUuid)
                .compose(RxUtil.applySchedulers())
                .doAfterTerminate(() -> {

                }).subscribe(this::postUnreadCount
                        , throwable -> showNetworkError(throwable, R.string.network_error, mView, mUserRepository)));
    }

    /**
     * 刷新数据
     */
    public void reqRefresh() {
        reqMessages(1);
    }

    /**
     * 加载更多
     */
    public void reqMore() {
        reqMessages(mPage + 1);
    }


    @Override
    public void cleanMessages() {
       /* mMessageRepository.deleteAllMessage()
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingView(true))
                .doAfterTerminate(() -> mView.hideLoadingView())
                .subscribe(entity -> {
                    mView.cleanSucc();
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.CLEAR));
                }, ex -> {
                    if (ex instanceof RequestError) {
                        RequestError error = ((RequestError) ex);
                        mView.cleanFail(error.getReturnCode(), error.getMessage());
                        return;
                    }
                    showNetworkError(ex, R.string.network_error, mView, mUserRepository);
                });*/
    }

    @Override
    public void reqOrderDetail(String orderUuid) {
        mDisposable.add(mOrderRepository.reqOrderDetail(orderUuid)
                .map(OrderVO::createFrom)
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingViewWithDelay(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(mView::openOrderByStatus
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }
}

