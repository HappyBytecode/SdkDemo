package anda.travel.driver.module.main.home;

import static anda.travel.driver.config.OrderStatus.ORDER_MAIN_STATUS_DOING;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.network.RequestParams;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.config.DutyStatus;
import anda.travel.driver.config.HxHomeStatus;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.data.ad.AdvertisementEntity;
import anda.travel.driver.data.dispatch.DispatchRepository;
import anda.travel.driver.data.duty.DutyRepository;
import anda.travel.driver.data.entity.HtmlActEntity;
import anda.travel.driver.data.entity.HxMessageEntity;
import anda.travel.driver.data.message.MessageRepository;
import anda.travel.driver.data.message.MessageSource;
import anda.travel.driver.data.order.OrderRepository;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.event.DispatchEvent;
import anda.travel.driver.event.MessageEvent;
import anda.travel.driver.event.NetworkEvent;
import anda.travel.driver.module.vo.DispatchVO;
import anda.travel.driver.module.vo.OrderVO;
import anda.travel.driver.util.DeviceUtil;
import anda.travel.driver.util.SpeechUtil;
import anda.travel.driver.util.zip.ZipManager;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * 功能描述：
 */
public class HomePresenter extends BasePresenter implements HomeContract.Presenter {

    private final HomeContract.View mView;
    private final OrderRepository mOrderRepository;
    private final UserRepository mUserRepository;
    private final MessageSource mMessageRepository;
    private final DispatchRepository mDispatchRepository;
    private final DutyRepository mDutyRepository;
    private boolean mIsFront;

    Timer timer = new Timer();
    TimerTask countDownTask;

    @Inject
    public HomePresenter(HomeContract.View view, OrderRepository orderRepository,
                         UserRepository userRepository, MessageRepository messageRepository,
                         DispatchRepository dispatchRepository, DutyRepository dutyRepository) {
        mView = view;
        mOrderRepository = orderRepository;
        mUserRepository = userRepository;
        mMessageRepository = messageRepository;
        mDispatchRepository = dispatchRepository;
        mDutyRepository = dutyRepository;
    }

    public void onCreate() {
        EventBus.getDefault().register(this);
        reqAllUnreadMessages(); //获取所有未读消息
        recording(); //更新设备信息
    }

    @Override
    public void subscribe() {
        super.subscribe();
        mIsFront = true;
        reqActModule();
        startTimeTask();
    }

    private void startTimeTask() {
        if (countDownTask != null) {
            countDownTask.cancel();
        }
        countDownTask = new TimerTask() {
            @Override
            public void run() {
                if (!mIsFront) return; //当前未显示，则不执行
                reqWorkInfo(); //获取首页统计信息
                reqHomeStatus(); //获取是否有进行中订单
            }
        };
        timer.schedule(countDownTask, 0, 30000);
    }

    public void stopTimer() {
        if (countDownTask != null) {
            countDownTask.cancel();
        }
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
        mIsFront = false;
        stopTimer();
    }

    public void onDestroy() {
        timer.cancel();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void reqWorkInfo() {
        mDisposable.add(mUserRepository.reqWorkInfo()
                .compose(RxUtil.applySchedulers())
                .doAfterTerminate(mView::hideRefreshing)
                .subscribe(vo -> {
                    mView.showHomePageInfo(vo);
                    if (vo.driverDispatchLog != null) {
                        mDispatchRepository.setDispatch(DispatchVO.createFrom(vo.driverDispatchLog));
                    }
                }, ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    @Override
    public void reqHomeStatus() {
        mDisposable.add(mOrderRepository.reqHomeStatus()
                .compose(RxUtil.applySchedulers())
                .subscribe(entity -> {
                    if (entity == null
                            || entity.status == null)
                        return;
                    switch (entity.status) {
                        case HxHomeStatus.DRV_HOMEPAGE_STATUS_DOGIN:
                            //提示"有进行中订单"
                            if (!TextUtils.isEmpty(entity.orderUuid)) {
                                mView.showOrderOngoing(entity.orderUuid);
                            }
                            break;
                        case HxHomeStatus.DRV_HOMEPAGE_STATUS_TIME_OUT:
                            //提示"有预约订单已超过出发时间"
                            if (!TextUtils.isEmpty(entity.orderUuid)) {
                                reqOrderDetailNew(entity.orderUuid, HxHomeStatus.DRV_HOMEPAGE_STATUS_TIME_OUT);
                            }
                            break;
                        case HxHomeStatus.DRV_HOMEPAGE_STATUS_APPO:
                            //提示"有预约订单即将开始"
                            if (!TextUtils.isEmpty(entity.orderUuid)) {
                                reqOrderDetailNew(entity.orderUuid, HxHomeStatus.DRV_HOMEPAGE_STATUS_APPO);
                            }
                            break;
                        case HxHomeStatus.DRV_HOMEPAGE_STATUS_NO_ORDER:
                            mView.hideOrderInfo();
                            break;
                    }
                }, ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    @Override
    public void reqOrderBegin(final String orderUuid) {
        //如果有调度任务，先结束调度
        mDispatchRepository.dispatchComplete(orderUuid);
        // 出发去接乘客
        mDisposable.add(mOrderRepository.reqPickUpPas(orderUuid)
                .map(OrderVO::createFrom)
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingViewWithDelay(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(mView::openOrderByStatus
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    @Override
    public void reqOrderDetail(String orderUuid) {
        reqOrderDetail(orderUuid, null);
    }

    @Override
    public void reqOrderDetail(String orderUuid, HxMessageEntity msgEntity) {
        mDisposable.add(mOrderRepository.reqOrderDetail(orderUuid)
                .map(OrderVO::createFrom)
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingViewWithDelay(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(vo -> {
                    if (vo.canPickUp == null) vo.canPickUp = 0;
                    if ((ORDER_MAIN_STATUS_DOING == vo.mainStatus || vo.canPickUp == 1)
                            && mDutyRepository.getIsOnDuty() == DutyStatus.OFF_DUTY_INT) {
                        mView.openOrderFailed();
                    } else {
                        mView.openOrderByStatus(vo);
                    }
                    /* 将消息设置为已读 */
                    if (msgEntity != null) readMessage(msgEntity);
                }, ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    @Override
    public void reqAllUnreadMessagesCount(int page) {
        mDisposable.add(mUserRepository
                .reqMessages(page)
                .compose(RxUtil.applySchedulers())
                .subscribe(messagesEntity -> mView.postUnreadCount(messagesEntity.noReadCount)
                        , throwable -> Timber.e(throwable.getMessage())));
    }

    @Override
    public void reqAllUnreadMessages() {
        //显示未读消息
        mDisposable.add(mMessageRepository.getUnreadMessage()
                .compose(RxUtil.applySchedulers())
                .subscribe(mView::showUnreadMessage
                        , ex -> Timber.e("reqAllUnreadMessages() ---> 执行失败！")));
    }

    @Override
    public void readMessage(HxMessageEntity entity) {
        mView.removeMessage(entity);
        mDisposable.add(mMessageRepository.readMessage(entity)
                .compose(RxUtil.applySchedulers())
                .subscribe(s -> Timber.d(MessageFormat.format("readMessage() ---> 执行结果：{0}", s))
                        , ex -> Timber.e("readMessage() ---> 执行失败！")));
    }

    @Override
    public void recording() {
        RequestParams.Builder builder = DeviceUtil.getDeviceInfo(mView.getContext());
        mDisposable.add(mUserRepository.recording(builder.build())
                .compose(RxUtil.applySchedulers())
                .subscribe(s -> {
                            if (s != null) {
                                if (!TextUtils.isEmpty(s.getPushType())) {
                                    mUserRepository.setPushType(Integer.valueOf(s.getPushType()));
                                } else {
                                    mUserRepository.setPushType(null);
                                }
                            } else {
                                mUserRepository.setPushType(null);
                            }
                        },
                        Throwable::printStackTrace));
    }

    @Override
    public DispatchVO getDispatchVO() {
        return mDispatchRepository.getDispatch();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkEvent(NetworkEvent event) {
        switch (event.type) {
            case NetworkEvent.CONNECT:
                mView.showNetworkNotice(false);
                break;
            case NetworkEvent.DISCONNECT:
                mView.showNetworkNotice(true);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.type) {
            case MessageEvent.NEW:
                reqAllUnreadMessages(); //刷新未读消息的显示
                reqAllUnreadMessagesCount(1);

                if (event.obj1 == null) return;
                if (event.obj1 instanceof String) {
                    String report = (String) event.obj1;
                    if (!TextUtils.isEmpty(report)) { //播报语音
                        SpeechUtil.speech(mView.getContext(), report);
                    }
                }
                break;
            case MessageEvent.CLEAR:
                mView.clearAllMessage(); //清除所有系统消息的显示
                break;
            case MessageEvent.REMIND: //有预约单提醒
                if (mIsFront) reqWorkInfo(); //刷新数据
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDispatchEvent(DispatchEvent event) {
        switch (event.type) {
            case DispatchEvent.DISPATCH_Refresh:
                Timber.d("-----> 刷新调度信息");
                reqWorkInfo();
                break;
            case DispatchEvent.DISPATCH_COMPLETE:
                reqWorkInfo();
                // 显示弹窗
                if (event.obj1 != null
                        && (!TextUtils.isEmpty((String) event.obj1))) {
                    mView.showDispatchCompleteDialog(((String) event.obj1));
                }
                break;
            case DispatchEvent.DISPATCH_REMIND:
                /////显示弹框提醒用户是否调度
                DispatchVO dispatchVO = null;
                if (event.obj1 != null
                        && (!TextUtils.isEmpty((String) event.obj1))) {
                    String disvo = (String) event.obj1;
                    dispatchVO = JSON.parseObject(disvo, DispatchVO.class);
                }
                mView.showDispatchRemindDialog(dispatchVO);
        }
    }

    @Override
    public void downloadHtml(Context context) {
        mDisposable.add(mUserRepository.reqActHtmlList()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                        vo -> {
                            for (HtmlActEntity item : vo) {
                                if ("rank".equals(item.getModuleCode()))
                                    getZip(context, item);
                            }
                        }
                        , ex -> {
                        }));
    }

    private void getZip(Context context, HtmlActEntity entity) {
        mDisposable.add(mUserRepository.reqActHtmlVersion(entity.getLastVersionNo(), entity.getUuid())
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(vo -> {
                            mUserRepository.setHtmlModule(vo);
                            ZipManager.getInstance().zipProcess(context, vo);
                        },
                        ex -> mUserRepository.setHtmlModule(null)
                ));
    }

    /**
     * 获取活动模块列表
     */
    private void reqActModule() {
        mDisposable.add(mUserRepository.reqActHtmlList()
                .compose(RxUtil.applySchedulers())
                .subscribe(
                        mView::showModuleList
                        , ex -> {
                        }
                )
        );
    }

    @Override
    public void getAD(boolean isUseCache) {
        if (isUseCache) {
            List<AdvertisementEntity> data = new ArrayList<>();
            try {
                data = mUserRepository.getAdCache();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (data != null && data.size() > 0) {
                jumpLogic(data);
            } else {
                getRemoteAd();
            }
        } else {
            getRemoteAd();
        }
    }

    private void getRemoteAd() {
        mDisposable.add(mUserRepository.getAd()
                .compose(RxUtil.applySchedulers())
                .subscribe(
                        this::jumpLogic
                        , Throwable::printStackTrace));
    }

    private void jumpLogic(List<AdvertisementEntity> data) {
        List<AdvertisementEntity> dialog = new ArrayList<>();
        List<AdvertisementEntity> bar = new ArrayList<>();
        for (AdvertisementEntity entity : data) {
            if (entity.getType() == IConstants.ADS_DIALOG) {
                dialog.add(entity);
            } else if (entity.getType() == IConstants.ADS_TRANSVERSE) {
                bar.add(entity);
            }
        }
        mView.skipToAd(dialog);
        mView.skipToAdTransverse(bar);
    }

    @Override
    public void reqOrderDetailNew(String orderUuid, int status) {
        mDisposable.add(mOrderRepository.reqOrderDetail(orderUuid)
                .map(OrderVO::createFrom)
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingViewWithDelay(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(vo -> {
                    if (status == HxHomeStatus.DRV_HOMEPAGE_STATUS_TIME_OUT) {
                        mView.showAppointBegin(vo);
                    } else {
                        mView.showAppointNotice(vo);
                    }
                }, ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    @Override
    public void getMainImages() {
        mDisposable.add(mUserRepository.mainImages()
                .compose(RxUtil.applySchedulers())
                .subscribe(
                        data -> {
                            mView.setMainImages(data);
                            if (data != null) {
                                if (!data.getIcon().isEmpty()) mView.hideDefaultModel();
                            }
                        }, ex -> {

                        }
                )
        );
    }

    /////判断是否出车
    public boolean isDuty() {
        return mDutyRepository.getIsOnDuty() == DutyStatus.ON_DUTY_INT;
    }
}
