package anda.travel.driver.module.order.ongoing;

import static anda.travel.driver.socket.SocketService.getInterval;

import android.os.Bundle;
import android.text.TextUtils;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import anda.travel.driver.BuildConfig;
import anda.travel.driver.R;
import anda.travel.driver.baselibrary.network.RequestError;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.common.AppManager;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.config.HxLateType;
import anda.travel.driver.config.OrderStatus;
import anda.travel.driver.data.duty.DutyRepository;
import anda.travel.driver.data.entity.PointEntity;
import anda.travel.driver.data.entity.SysConfigEntity;
import anda.travel.driver.data.entity.UploadPointEntity;
import anda.travel.driver.data.order.OrderRepository;
import anda.travel.driver.data.uploadpoint.UploadPointRepository;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.event.DutyEvent;
import anda.travel.driver.event.MapEvent;
import anda.travel.driver.event.OrderEvent;
import anda.travel.driver.event.SlideViewEvent;
import anda.travel.driver.module.amap.assist.CalculateUtils;
import anda.travel.driver.module.vo.OrderVO;
import anda.travel.driver.service.RecordingService;
import anda.travel.driver.socket.SocketEvent;
import anda.travel.driver.socket.SocketPushContent;
import anda.travel.driver.socket.SocketService;
import anda.travel.driver.socket.message.GetLocationOrderResponseMessage;
import anda.travel.driver.util.OrderManager;
import anda.travel.driver.util.SpeechUtil;
import anda.travel.driver.util.SysConfigUtils;
import anda.travel.driver.widget.NoticeDialog;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * 功能描述：
 */
public class OrderOngoingPresenter extends BasePresenter implements OrderOngoingContract.Presenter {

    private final static int MINUTES = 1000 * 60; //1分钟对应的毫秒数
    private Integer mRetainDistance; // 20170808增加（单位：米）
    private final OrderOngoingContract.View mView;
    private final OrderRepository mOrderRepository;
    private final UserRepository mUserRepository;
    private final DutyRepository mDutyRepository;
    private final UploadPointRepository mUploadPointRepository;
    private String mOrderUuid; //订单编号
    private boolean mNeedReported = true; //是否已提醒"快到达目的地"
    private Long mDepartTimeStamp; //记录前往目的地的时间
    private SysConfigEntity mSysConfig;
    private boolean isArrive;
    private double mLastTripDistance;
    private LatLng mLastPosition;
    private Long mLateTime;
    private Long mRealLateTime;
    private Subscription mLateSubscription;
    private Subscription mCheckSubscription;

    @Inject
    public OrderOngoingPresenter(OrderOngoingContract.View view, OrderRepository orderRepository,
                                 UserRepository userRepository,
                                 DutyRepository dutyRepository, UploadPointRepository uploadPointRepository) {
        mView = view;
        mOrderRepository = orderRepository;
        mUserRepository = userRepository;
        mDutyRepository = dutyRepository;
        mUploadPointRepository = uploadPointRepository;
    }

    public void eventStart() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    /**
     * onCreateView中调用
     */
    public void onCreate(String orderUuid) {
        mOrderUuid = orderUuid; //记录订单编号
        OrderVO vo = OrderManager.instance().getOrderVO();
        //继续计费时，为优化显示添加
        if (vo != null && vo.tempPrice != null && vo.tempPrice > 0) {
            mView.setTotalPrice(vo.tempPrice);
        }
        //获取orderUuid对应的"最近一次上传的位置信息"
        EventBus.getDefault().post(new SocketEvent(SocketEvent.GET_LAST_SPECIAL_INFO, mOrderUuid));
        /////刚进入页面如果是出发状态，则开启Service
        if (vo != null && vo.subStatus == OrderStatus.DEPART) {
            soundRecording();
        }

        if (mCheckSubscription == null) {
            mCheckSubscription = Observable.interval(SocketService.getInterval(), TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        checkSpeech();
                        if (OrderManager.instance().getSubStatus() == OrderStatus.DEPART) {
                            if (null != mCheckSubscription) {
                                mCheckSubscription.unsubscribe();
                            }
                        }
                    }, ex -> {
                    });
        }

        mSysConfig = SysConfigUtils.get().getSysConfig();
        if (mSysConfig != null) {
            if ("1".equals(mSysConfig.getImSwitch())) {

            }
        }

        mDutyRepository.orderOngoingCreate(); //有订单进行中
        // 解决接单成功后接乘客路径规划两遍的问题
//        mView.setDisplay(); //设置显示
    }

    @Override
    public void subscribe() {
        super.subscribe();
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
    }

    /**
     * onDestoryView中调用
     */
    public void onDestory() {
        if (mCheckSubscription != null) {
            mCheckSubscription.unsubscribe();
        }
        cancelLateOperate();
        ////关掉定时器
        EventBus.getDefault().unregister(this);
        mDutyRepository.orderOngoingDestory(); //非进行中
        if (RecordingService.Companion.getInstance() != null) {
            RecordingService.Companion.getInstance().stopRecording();
        }
        if (mSysConfig != null) {
            if ("1".equals(mSysConfig.getImSwitch())) {

            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDutyEvent(DutyEvent event) {
        if (event.type == DutyEvent.BACKGROUND) {
            if (OrderManager.instance().isDepart()) {
                //只有在行程中，才语音提醒
                mView.speechAppInBackground();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderEvent(OrderEvent event) {
        switch (event.type) {
            case OrderEvent.LAST_SPECIAL_INFO:
                if (event.obj1 == null) return;
                GetLocationOrderResponseMessage msg = (GetLocationOrderResponseMessage) event.obj1;
                if (!msg.getOrderUuid().equals(mOrderUuid)) return;
                getLastTripData(msg);
                if (msg.getTotalFare() != null) mView.setTotalPrice(msg.getTotalFare()); //设置金额显示
                break;
            case OrderEvent.LOCAL_CANCEL: //订单已取消
                //20180627 关闭导航页
                EventBus.getDefault().post(new OrderEvent(OrderEvent.CLOSE_NAVI));
                mView.closeActivity(); //关闭界面
                break;
            case OrderEvent.ORDER_PASSENGER_CANCEL: //取消订单
                if (event.obj1 == null) return;
                SocketPushContent push = (SocketPushContent) event.obj1;
                if (!push.data.orderUuid.equals(mOrderUuid)) return;
                //20180627 关闭导航页
                EventBus.getDefault().post(new OrderEvent(OrderEvent.CLOSE_NAVI));
                mView.closeActivity(); //关闭界面
                break;
            case OrderEvent.SPECIAL_PRICE: //专车金额
                if (event.obj1 == null || event.obj2 == null) return;
                String orderUuid = (String) event.obj1;
                if (!orderUuid.equals(mOrderUuid)) return;
                double totalPrice = (double) event.obj2;
                mView.setTotalPrice(totalPrice);
                /* 订单被改派 */
                if (event.obj3 != null && (boolean) event.obj3) {
                    mView.showOrderDistributToOther(OrderManager.instance().getOrderVO().getDistributeToOtherNotice()); //显示相应的弹窗
                    //20180627 关闭导航页
                    EventBus.getDefault().post(new OrderEvent(OrderEvent.CLOSE_NAVI));
                }
                break;
            case OrderEvent.ORDER_DISTRIBUTE_TO_OTHER: //订单被改派
                if (event.obj1 == null) return;
                SocketPushContent pushContent = (SocketPushContent) event.obj1;
                if (!mOrderUuid.equals(pushContent.orderUuid)) return;
                mView.showOrderDistributToOther(OrderManager.instance().getOrderVO().getDistributeToOtherNotice()); //显示相应的弹窗
                //20180627 关闭导航页
                EventBus.getDefault().post(new OrderEvent(OrderEvent.CLOSE_NAVI));
                break;
            //////////////异常订单提示 短单长跑
            case OrderEvent.ORDER_ABNORMAL:
                mView.showAbnormalView();
                break;
            case OrderEvent.ORDER_CROSS_CITY:
                mView.showCrossCityView();
                break;
            ///////更改目的地
            case OrderEvent.ORDER_CHANGE_ADDRESS:
                ///////////调用订单详情刷新
                reqOrderDetail();
                ////////////////判断是否此页面在前台，再播放语音
                if (AppManager.getInstance().currentActivity() instanceof OrderOngoingActivity) {
                    String address = (String) event.obj1;
                    String speak = mView.getContext().getString(R.string.change_address_navigation, address);
                    SpeechUtil.speech(speak);
                }
                break;
        }
    }

    private void getLastTripData(GetLocationOrderResponseMessage msg) {
        //////////从数据库里面拿出之前跑的数据
        UploadPointEntity entity = mUploadPointRepository.getLatelyPoint(mOrderUuid);

        if (msg == null) {
            if (entity != null) {
                mLastTripDistance = entity.getMileage();
                mLastPosition = new LatLng(entity.getLat(), entity.getLng());
            }
        } else if (msg.getLat() == null
                || msg.getLat() == 0
                || msg.getLng() == null
                || msg.getLng() == 0) {
            if (entity != null) {
                mLastTripDistance = entity.getMileage();
                mLastPosition = new LatLng(entity.getLat(), entity.getLng());
            }
        } else {
            if (entity != null) {
                if (msg.getMileage() < 0.3 && entity.getMileage() > 0.3) {
                    mLastTripDistance = entity.getMileage();
                    mLastPosition = new LatLng(entity.getLat(), entity.getLng());
                } else {
                    if (msg.getMileage() < entity.getMileage()) {
                        //服务端的上次里程小于本地的，以本地的里程作为上次里程
                        mLastTripDistance = entity.getMileage();
                        mLastPosition = new LatLng(entity.getLat(), entity.getLng());
                    } else {
                        //服务端的上次里程大于本地的，以服务端的里程作为上次里程
                        mLastTripDistance = msg.getMileage();
                        mLastPosition = new LatLng(msg.getLat(), msg.getLng());
                    }
                }
            } else {
                mLastTripDistance = msg.getMileage();
                mLastPosition = new LatLng(msg.getLat(), msg.getLng());
            }
        }
        LatLng mCurrentPoint = mUserRepository.getLatLng();
        double mCenterTripDistance = CalculateUtils.calculateLineDistance(mLastPosition, mCurrentPoint) / 1000.0;
        ////////////防止用户上一次这一单在合肥市，在非合肥市结束订单的时候操作
        if (mCenterTripDistance < 100) {
            ///////测试的时候由于当前定位不动，就不加入两点之间的距离了
            if (BuildConfig.DEBUG) {
                OrderManager.instance().setLastMileage(mLastTripDistance);
            } else {
                OrderManager.instance().setLastMileage(mLastTripDistance + mCenterTripDistance);
            }
        } else {
            OrderManager.instance().setLastMileage(mLastTripDistance);
        }
    }

    public void setLastInfoFromDb(String orderUuid) {
        //////////从数据库里面拿出之前跑的数据
        UploadPointEntity entity = mUploadPointRepository.getLatelyPoint(orderUuid);
        if (entity != null) {
            OrderManager.instance().setLastMileage(entity.getMileage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMapEvent(MapEvent event) {
        switch (event.type) {
            case MapEvent.VIEW_RESET: //重置信息显示
                mRetainDistance = null;
                mView.setNaviInfoDisplay(null, null);
                break;
            case MapEvent.VIEW_NaviInfoUpdate: //设置显示
                if (event.obj1 == null) return;
                NaviInfo naviInfo = (NaviInfo) event.obj1;
                mRetainDistance = naviInfo.getPathRetainDistance();
                if (isArrive) {
                    mView.setNaviInfoDisplay(0, 0);
                } else {
                    mView.setNaviInfoDisplay(mRetainDistance, naviInfo.getPathRetainTime());
                }
                if (mNeedReported
                        && mRetainDistance < 200 //小于200米
                        && mDepartTimeStamp != null
                        && (System.currentTimeMillis() - mDepartTimeStamp > 10000)) {
                    mNeedReported = false;
                    SpeechUtil.speech(R.string.order_near_dest);
                }
                break;
            case MapEvent.VIEW_LocationChange: //位置改变
                if (event.obj1 == null) return;
                AMapNaviLocation location = (AMapNaviLocation) event.obj1;
                dealWithAMapNaviLocation(location);
                break;
            case MapEvent.VIEW_RECALCULATE://偏航回调
                if (OrderManager.instance().isDepart()) {
                    carpoolReCalculateRoute(mOrderUuid);
                }
                break;
        }
    }

    private void carpoolReCalculateRoute(String mOrderUuid) {
        mOrderRepository.carpoolReCalculateRoute(mOrderUuid)
                .compose(RxUtil.applySchedulers())
                .subscribe(s -> Timber.e("调用偏离路线接口成功")
                        , ex -> Timber.e("调用偏离路线接口成功"));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSlideViewEvnet(SlideViewEvent event) {
        switchToNextStatus();
    }

    @Override
    public void reqOrderDetail() {
        mDisposable.add(mOrderRepository.reqOrderDetail(mOrderUuid)
                .map(OrderVO::createFrom)
                .compose(RxUtil.applySchedulers())
                .subscribe(vo -> {
                    OrderManager.instance().setOrderVO(vo);
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
                    mView.setDisplay();
                }, ex -> {
                }));
    }

    @Override
    public void switchToNextStatus() {
        switch (OrderManager.instance().getSubStatus()) {
            case OrderStatus.WAIT_ARRIVE_ORIGIN:
                reqArriveStart();
                break;
            case OrderStatus.WATI_PASSENGER_GET_ON:
                reqPassengerGetOn();
                break;
            case OrderStatus.DEPART:
                reqArriveEnd();
                break;
            default:
                mView.resetBtnDisplay(); //重置按键显示
                break;
        }
    }

    @Override
    public void reqArriveStart() {//到达上车地点
        mDisposable.add(mOrderRepository.reqDepart(mOrderUuid)
                .map(OrderVO::createFrom)
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingView(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(vo -> {
                    OrderManager.instance().setOrderVO(vo);
                    mView.arriveStart();
                }, ex -> {
                    mView.resetBtnDisplay(); //重置按键显示
                    showNetworkError(ex, R.string.network_error, mView, mUserRepository);
                    dealWithStatusError(ex); //订单状态错误时，刷新订单详情
                }));
    }

    @Override
    public void lateBilling() {
        mDisposable.add(mOrderRepository.lateBilling(mOrderUuid)
                .map(OrderVO::createFrom)
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingViewWithDelay(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(vo -> {
                    OrderManager.instance().setOrderVO(vo);
                    mView.toast("开始计算超时等待费");
                    mView.setDisplay();
                }, ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    @Override
    public void reqPassengerGetOn() {
        mDisposable.add(mOrderRepository.reqGeton(mOrderUuid)
                .map(OrderVO::createFrom)
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingView(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(vo -> {
                    OrderManager.instance().setOrderVO(vo);
                    mView.passengerGetOn();
                    ////乘客上车后开始录音
                    soundRecording();
                    cancelLateOperate();
                }, ex -> {
                    mView.resetBtnDisplay(); //重置按键显示
                    showNetworkError(ex, R.string.network_error, mView, mUserRepository);
                    dealWithStatusError(ex); //订单状态错误时，刷新订单详情
                }));
    }

    @Override
    public void reqArriveEnd() {
        //////////当前的经纬度和终点的经纬度距离判断是否超过后台设定的值，如果超过则弹框
        double distance = getDistanceFromEnd();
        if (mSysConfig == null) {
            ArriveEndLogic();
        } else {
            try {
                if (!TextUtils.isEmpty(mSysConfig.getWarnDistance()) &&
                        distance >= Float.parseFloat(mSysConfig.getWarnDistance())) {
                    NoticeDialog noticeDialog = new NoticeDialog(mView.getContext());
                    noticeDialog.setNoticeTitle(mView.getContext().getString(R.string.far_title)).
                            setNoticeContent(mView.getContext().getString(R.string.far_content))
                            .setNoticeContentColor(R.color.notice_orange)
                            .setImageRes(R.drawable.ic_notice_far)
                            .setConfirmText(mView.getContext().getString(R.string.far_confirm)).setConfirmListener(() -> {
                        //////提交前判断用户不关掉弹框，此时继续开，而此时距离不超过，则不提交接口
                        double newDistance = getDistanceFromEnd();
                        if (newDistance >= Float.parseFloat(mSysConfig.getWarnDistance())) {
                            ////调用接口，继续执行
                            mDisposable.add(mOrderRepository.orderDistanceWarn(mOrderUuid, distance)
                                    .compose(RxUtil.applySchedulers())
                                    .subscribe(s -> {
                                    }, ex -> {
                                    }));
                            ArriveEndLogic();
                        } else {
                            ArriveEndLogic();
                        }
                        isArrive = true;
                    }).setCancelListener(() -> {

                    });
                    noticeDialog.show();
                    mView.resetBtnDisplay();
                } else {
                    ArriveEndLogic();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void ArriveEndLogic() {
        if (RecordingService.Companion.getInstance() != null) {
            RecordingService.Companion.getInstance().stopRecording();
        }
        mDisposable.add(mOrderRepository.reqArrive(mOrderUuid,
                OrderManager.instance().getMileage(), 2)
                .map(OrderVO::createFrom)
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingView(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(vo -> {
                    mUploadPointRepository.deleteAllPoints();
                    /////////行程结束可以释放资源了
                    OrderManager.instance().releaseData();
                    ///////////////确认到达目的地后再上传录音文件
                    if (RecordingService.Companion.getInstance() != null) {
                        RecordingService.Companion.getInstance().upLoadAudio(mOrderUuid);
                    }
                    mView.arriveEnd(vo);
                }, ex -> {
                    Timber.d("订单状态错误");
                    mView.resetBtnDisplay(); //重置按键显示
                    showNetworkError(ex, R.string.network_error, mView, mUserRepository);
                    dealWithStatusError(ex); //订单状态错误时，刷新订单详情  ///////////////确认到达目的地后再上传录音文件
                    if (RecordingService.Companion.getInstance() != null) {
                        RecordingService.Companion.getInstance().upLoadAudio(mOrderUuid);
                    }
                }));
    }

    /////////////////刷新获得正确的订单状态
    @Override
    public void dealWithStatusError(Throwable ex) {
        if (ex instanceof RequestError) {
            ////////因为上一次滑动操作，后台超时，其实状态已经改变导致的问题
            mDisposable.add(mOrderRepository.reqOrderDetail(mOrderUuid)
                    .map(OrderVO::createFrom)
                    .compose(RxUtil.applySchedulers())
                    .subscribe(vo -> {
                        /////订单被取消
                        if (vo.mainStatus == OrderStatus.ORDER_MAIN_STATUS_CANCEL) {
                            EventBus.getDefault().post(new OrderEvent(OrderEvent.CLOSE_NAVI));
                            mView.closeActivity();
                        }

                        switch (vo.subStatus) {
                            case OrderStatus.WATI_PASSENGER_GET_ON:
                                OrderManager.instance().setOrderVO(vo);
                                mView.arriveStart();
                                break;
                            case OrderStatus.DEPART:
                                //////防止滑动时候接口请求超时发生错误的时候，但是通过订单详情发现已经到了行程中的时候，需要取消
                                //等待乘客的操作线程
                                cancelLateOperate();
                                OrderManager.instance().setOrderVO(vo);
                                mView.passengerGetOn();
                                ////乘客上车后开始录音
                                soundRecording();
                                break;
                            case OrderStatus.ARRIVE_DEST:
                                //////////已经到达目的地
                                mUploadPointRepository.deleteAllPoints();
                                mView.arriveEnd(vo);
                                /////////行程结束可以释放资源了
                                OrderManager.instance().releaseData();
                                break;
                        }
                    }, exception -> {
                    }));
        }
    }

    @Override
    public LatLng getCurrentLatLng() {
        return mUserRepository.getLatLng();
    }

    @Override
    public void dealWithAMapNaviLocation(AMapNaviLocation location) {
        try {
            if (location.getCoord() != null
                    && location.getCoord().getLongitude() != 0
                    && location.getCoord().getLatitude() != 0
            ) {
                if (OrderManager.instance().isDepart()) { //保存轨迹点
                    PointEntity point = new PointEntity();
                    point.setLat(location.getCoord().getLatitude());
                    point.setLon(location.getCoord().getLongitude());
                    point.setBearing(location.getBearing());
                    point.setSpeed(location.getSpeed());
                    point.setLoctime(System.currentTimeMillis());
                    point.setAccuracy(location.getAccuracy());
                    point.setMatchStatus(location.isMatchNaviPath());
                    OrderManager.instance().addPoint(point);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isEmulatorOpen() {
        return mUserRepository.getDebugEmulator();
    }

    @Override
    public void dealWithLateTime(Long lateTime, Long realLateTime) {
        mLateTime = lateTime;
        mRealLateTime = realLateTime;
        setLateTimeDisplay(mLateTime); //控制显示
        if (mLateSubscription == null) {
            mLateSubscription = Observable.interval(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        if (mLateTime != null) {
                            mLateTime += 1000;
                        }
                        setLateTimeDisplay(mLateTime);
                    }, ex -> {
                        mView.toast("程序异常，等待计时停止，请重启app");
                    });
        }
    }

    @Override
    public void recordDepartTimeStamp() {
        if (mDepartTimeStamp == null) {
            mDepartTimeStamp = System.currentTimeMillis();
        }
    }

    private void setLateTimeDisplay(long lateTime) {
        OrderVO vo = OrderManager.instance().getOrderVO();
        if (vo != null) {
            long time = vo.freeWaitTime == null
                    ? lateTime
                    : lateTime - vo.freeWaitTime;
            if (time <= 0) {
                int minute = (int) (Math.abs(time) / MINUTES);
                if (time % MINUTES != 0) minute += 1; //向前取整

                mView.showLateTime(minute + "分钟", HxLateType.NORMAL);
            } else {
                if (vo.isLateBilling != null
                        && vo.isLateBilling == HxLateType.BILLING) {
                    mView.showLateTime("迟到计费中: ", HxLateType.BILLING);
                } else {
                    mView.showLateTime("乘客超时,点击开始计费", HxLateType.LATE);
                }
            }
            /////防止预约单提前到达的时候显示错误的等待时间
            if (lateTime > 0) {
                /////每过1s发送消息
                EventBus.getDefault().post(new MapEvent(MapEvent.VIEW_WAIT_TIME, lateTime));
            }
        }
    }

    /* ***** 20170808 增加 ***** */
    private final static int LATE_REPORT_TIME = 5; //（单位：分钟）乘客迟到多久后，播报提示
    private final static int UNIT = (getInterval() >= MINUTES) ? 1 : (MINUTES / getInterval()); //至少为1
    private int mCount = -1;
    private long mNearOriginReportTime; //播报时间
    private int mLateReportInt = LATE_REPORT_TIME; //播报的迟到时间

    // 定时检测需播报的内容
    private void checkSpeech() {
        try {
            switch (OrderManager.instance().getSubStatus()) {
                case OrderStatus.WAIT_ARRIVE_ORIGIN: //正在前往"上车地点"
                    if (mRetainDistance != null && mRetainDistance < 100 //距离小于100米
                            && (System.currentTimeMillis() - mNearOriginReportTime > MINUTES)) { //间隔时间超过1分钟
                        //记录播报时间
                        mNearOriginReportTime = System.currentTimeMillis();
                        //播报语音
                        SpeechUtil.speech(R.string.speech_order_near_origin);
                    }
                    break;
                case OrderStatus.WATI_PASSENGER_GET_ON: //到达"上车地点"
                    mCount++;
                    if (OrderManager.instance().getOrderVO().isLateBilling != null && OrderManager.instance().getOrderVO().isLateBilling == HxLateType.BILLING) { //已经开始迟到计费
                        if (mRealLateTime != null && (mRealLateTime / MINUTES >= mLateReportInt)
                                && (mRealLateTime % MINUTES) <= (MINUTES / 3)) {
                            mLateReportInt = (int) (mRealLateTime / MINUTES + 2);
                            SpeechUtil.speech("乘客已迟到" + mRealLateTime / MINUTES + "分钟");
                        }
                    } else {
                        if (mCount == 0) {
                            //第一次计时进来忽略
                            return;
                        }
                        if (mCount % UNIT == 0) { //每隔1分钟触发一次
                            if (getDistanceFromGeton() > 200) { //是否已远离"上车地点"
                                //播报语音
                                SpeechUtil.speech(R.string.speech_order_need_start);
                            } else {
                                if (mLateTime != null
                                        && OrderManager.instance().getOrderVO() != null
                                        && (mLateTime > (OrderManager.instance().getOrderVO().freeWaitTime != null ? OrderManager.instance().getOrderVO().freeWaitTime : 0))) { //超过免费等待时间
                                    //播报语音
                                    SpeechUtil.speech(R.string.speech_order_late);
                                }
                            }
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 距离（单位：米）
    private float getDistanceFromGeton() {
        OrderVO vo = OrderManager.instance().getOrderVO();
        if (vo == null
                || vo.drvArrLat == null
                || vo.drvArrLng == null
                || vo.drvArrLat == 0
                || vo.drvArrLng == 0
        ) {
            return 0;
        }
        return CalculateUtils.calculateLineDistance(OrderManager.instance().getLatLng(),
                new LatLng(vo.drvArrLat, vo.drvArrLng));
    }

    //当前距离终点距离（单位：米）
    private float getDistanceFromEnd() {
        try {
            return CalculateUtils.calculateLineDistance(OrderManager.instance().getLatLng(),
                    new LatLng(OrderManager.instance().getOrderVO().destLat, OrderManager.instance().getOrderVO().destLng));
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("vo", OrderManager.instance().getOrderVO());
        UploadPointEntity entity = mUploadPointRepository.getLatelyPoint(mOrderUuid);
        if (entity != null) {
            outState.putParcelable("lastPoint", new LatLng(entity.getLat(), entity.getLng()));
            outState.putDouble("lastMile", entity.getMileage());
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {//页面重新创建时，恢复里程信息

        }
    }

    @Override
    public void startRecording() {
        RecordingService.Companion.startService(mView.getContext(), mOrderUuid);
    }

    public String getSlideViewBtnText(Integer status) {
        String btn = "";
        switch (status) {
            case OrderStatus.WAIT_ARRIVE_ORIGIN:
                btn = "到达上车地点";
                break;
            case OrderStatus.WATI_PASSENGER_GET_ON:
                btn = "开始行程";
                break;
            case OrderStatus.DEPART:
                btn = "到达目的地";
                break;
        }
        return btn;
    }

    ///取消迟到的子线程(到了下一步，或者取消订单之后)
    public void cancelLateOperate() {
        mCount = -1;
        mLateTime = 0L;
        /////////取消迟到操作
        if (mLateSubscription != null) {
            mLateSubscription.unsubscribe();
        }
        EventBus.getDefault().post(new MapEvent(MapEvent.VIEW_WAIT_TIME_CANCEL));
    }

    /////判断乘客是否授权能够开始录音，如果可以录音就开始录音
    public void soundRecording() {
        try {
            SysConfigEntity sysConfigEntity = SysConfigUtils.get().getSysConfig();
            if (null != sysConfigEntity && 1 == sysConfigEntity.getOrderAudioSwitch()) {
                mDisposable.add(mOrderRepository.authStatus(mOrderUuid, "2")
                        .compose(RxUtil.applySchedulers())
                        .subscribe(s -> {
                            /////调用接口成功.0未授权 1 已授权 2 已取消授权
                            if (s == 1) {
                                mView.audioPermission();
                            }
                        }, ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
