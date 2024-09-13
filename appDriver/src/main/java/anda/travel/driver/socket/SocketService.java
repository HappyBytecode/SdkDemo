package anda.travel.driver.socket;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.alibaba.fastjson.JSON;
import com.amap.api.navi.model.NaviInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.client.constants.MessageType;
import anda.travel.driver.client.message.AndaMessage;
import anda.travel.driver.client.message.Header;
import anda.travel.driver.client.message.body.GetPosition;
import anda.travel.driver.client.message.body.HeartBeat;
import anda.travel.driver.client.message.body.Login;
import anda.travel.driver.client.message.body.RespPush;
import anda.travel.driver.client.message.body.UploadPosition;
import anda.travel.driver.client.message.body.UploadPositionList;
import anda.travel.driver.client.message.body.UploadRoute;
import anda.travel.driver.common.AppConfig;
import anda.travel.driver.common.AppManager;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.config.DutyStatus;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.config.OrderStatus;
import anda.travel.driver.data.dispatch.DispatchRepository;
import anda.travel.driver.data.duty.DutyRepository;
import anda.travel.driver.data.entity.HxMessageEntity;
import anda.travel.driver.data.entity.SysConfigEntity;
import anda.travel.driver.data.entity.UploadPointEntity;
import anda.travel.driver.data.message.MessageRepository;
import anda.travel.driver.data.order.OrderRepository;
import anda.travel.driver.data.uploadpoint.UploadPointRepository;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.event.AwakenEvent;
import anda.travel.driver.event.DutyEvent;
import anda.travel.driver.event.GaoDeServiceEvent;
import anda.travel.driver.event.MessageEvent;
import anda.travel.driver.module.main.MainActivity;
import anda.travel.driver.module.order.price.PriceCheckActivity;
import anda.travel.driver.socket.message.UploadLocationMessage;
import anda.travel.driver.socket.utils.InfoUtils;
import anda.travel.driver.socket.utils.LocUtils;
import anda.travel.driver.socket.utils.NetLimitUtils;
import anda.travel.driver.sound.SoundUtils;
import anda.travel.driver.util.DeviceUtil;
import anda.travel.driver.util.Navigate;
import anda.travel.driver.util.OrderManager;
import anda.travel.driver.util.SpeechUtil;
import anda.travel.driver.util.SysConfigUtils;
import anda.travel.driver.baselibrary.utils.BackgroundUtil;
import anda.travel.driver.baselibrary.utils.NetworkUtil;
import anda.travel.driver.baselibrary.utils.NumberUtil;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.baselibrary.utils.SP;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * 1.控制长连接的开启或关闭
 * 2.定时上传位置或发送心跳包
 * 3.控制其它消息的发送
 */
public class SocketService extends Service {

    public final static int INTERVAL = 10 * 1000; //定时器的时间间隔

    private static String wsUrl = ""; //长链接地址（需在onCreate中设置）
    private static SocketService mInstance;
    private static ServiceReadyListener mListener;
    private boolean mHasUnConnection;

    public static void startService(Context context, ServiceReadyListener listener) {
        mListener = listener;
        if (isReady() && mListener != null) listener.onReady();
        startService(context); //启动SocketService
    }

    private static void startService(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN).setClass(context, SocketService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void checkServiceWithStart(Context context) {
        if (isReady()) return; //如果已启动，不执行
        startService(context); //如果已关闭，则重新启动
    }

    public static SocketService getInstance() {
        return mInstance;
    }

    /**
     * 为避免内存泄漏，在适当的时机移除监听
     */
    public static void removeServiceListener() {
        mListener = null;
    }

    /**
     * SocketService是否已启动
     *
     * @return
     */
    public static boolean isReady() {
        return mInstance != null;
    }

    @Inject
    public UserRepository mUserRepository; //用户仓库
    @Inject
    public DutyRepository mDutyRepository; //出车收车仓库
    @Inject
    public MessageRepository mMessageRepository; //消息仓库
    @Inject
    public DispatchRepository mDispatchRepository; //调度仓库
    @Inject
    public UploadPointRepository mUploadPointRepository;
    @Inject
    public SP mSP;
    @Inject
    public OrderRepository mOrderRepository;
    private ISocket mClient;

    Timer timer = new Timer();
    TimerTask countDownTask;

    private void startTimeTask() {
        if (countDownTask != null) {
            countDownTask.cancel();
        }
        countDownTask = new TimerTask() {
            @Override
            public void run() {
                if (mUserRepository.isLogin()) { //已登录
                    int appStatus;
                    if (mClient != null && mClient.isSocketOpen()) { //长连接处于开启状态
                        boolean isSocketAvailable = mClient.timerOperation(); //长连接是否正常（以"是否长时间未收到推送消息"为判断标准）
                        if (isSocketAvailable) { //长连接正常
                            appStatus = timerUploadOperation(); //执行定时操作（上传位置信息或发送心跳包）
                        } else { //长连接异常，长时间收不到推送消息(含心跳包)，则重连
                            connect(); //主动连接
                            appStatus = BackgroundUtil.getAppStatus(SocketService.this);
                        }
                    } else { //长连接处理关闭状态
                        if (NetworkUtil.isNetworkAvailable(SocketService.this))
                            connect(); //网络正常，则重连
                        appStatus = BackgroundUtil.getAppStatus(SocketService.this);
                        ////长连接异常的情况下，判断是否需要往数据库存入点
                        UploadLocationMessage message = OrderManager.instance().getUploadLocationMessage(SocketService.this, mDutyRepository, mDispatchRepository, mUserRepository);
                        if (message != null) {
                            String orderid = message.getOrderUuid();
                            if (!TextUtils.isEmpty(orderid)) {
                                mHasUnConnection = true;
                                mUploadPointRepository.insertPoint(message);
                            }
                        }
                    }
                    mDutyRepository.updateDutyTime(null); //更新出车时长
                    mDutyRepository.updateDutyLog(false, appStatus); //更新出车信息
                } else { //未登录
                    if (mClient != null) disconnect(); //主动关闭长连接
                }
            }
        };
        timer.schedule(countDownTask, 0, getInterval());
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        wsUrl = AppConfig.WS;
        Timber.d(MessageFormat.format("SocketService 已启动！\nwsUrl = {0}", wsUrl));

        if (mListener != null) mListener.onReady();
        EventBus.getDefault().register(this); //注册监听
        HxClientManager.getAppComponent().inject(this); //依赖注入
        LocUtils.get().init(mSP, mUserRepository);
        LocUtils.get().startLocation(this); //开启定位
        //handler.postDelayed(timerRun, getInterval()); //开启定时
        startTimeTask();
        startForeground(); //启动前台Service
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.d("SocketService 已销毁！");
        stopForeground(); //关闭前台Service

        EventBus.getDefault().unregister(this); //注销监听
        stopTimer();

        disconnect(); //关闭长连接
        LocUtils.get().stopLocation(); //关闭定位
        LocUtils.get().reset();
        InfoUtils.get().reset(); //重置缓存的用户信息

        mDispatchRepository.destoryNavi(); //关闭调度
        mClient = null;
        mInstance = null;
    }

    public void reset() {
        startTimeTask();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public interface ServiceReadyListener {
        void onReady();
    }

    private long mRouteId;
    private int mRouteIndex;
    private int mRetainDistance = 0;
    private int mRetainTime = 0;

    /**
     * 监听"连接"或"断开"的消息
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocketEvent(SocketEvent event) {
        switch (event.type) {
            case SocketEvent.CONNECT_ERROR: //连接异常
                NetLimitUtils.checkConnect(this, mUserRepository, mDutyRepository);
                break;
            case SocketEvent.DISCONNECT: //断开连接
                disconnect();
                break;
            case SocketEvent.LOGOUT: //退出登录
                mUserRepository.logout();
//                Navigate.openLogin(this); //跳转到登录页
                AppManager.getInstance().finishAllActivity(); //关闭所有界面（除登录页）
                break;
            case SocketEvent.CONNECT: //开启连接
                if (mClient != null && mClient.isSocketOpen()) {
                    //检查用户和token是否一致
                    mUserRepository.getUserInfo()
                            .compose(RxUtil.applySchedulers())
                            .subscribe(entity -> {
                                /* token不一致，则重连 */
                                if (!entity.token.equals(InfoUtils.get().getToken())) {
                                    connect();
                                }
                            }, ex -> {
                            });

                } else {
                    connect(); //开启长连接
                }
                break;
            case SocketEvent.GET_LAST_SPECIAL_INFO: //获取专车最近一次上传的信息
                if (event.obj1 == null) return;
                sendGetLocationOrder((String) event.obj1);
                break;
            case SocketEvent.UPLOAD_LOCATION_RIGHT_NOW: //马上上传位置信息
                sendLocationMessage(
                        OrderManager.instance().getUploadLocationMessage(
                                this, mDutyRepository, mDispatchRepository, mUserRepository
                        )
                );
                break;
            case SocketEvent.UPLOAD_ROUTE_POINTS: //路径规划时发来的点集合
                sendRoutePoints((String) event.obj1, (long) event.obj2);
                break;
            case SocketEvent.VIEW_NAVI_INFO_UPDATE: //实时更新的导航信息
                NaviInfo naviInfo = (NaviInfo) event.obj1;
                if (naviInfo == null) return;
                mRouteId = naviInfo.getPathId();
                mRetainDistance = naviInfo.getPathRetainDistance();
                mRetainTime = naviInfo.getPathRetainTime();
                break;
            case SocketEvent.UPLOAD_DRIVER_INDEX: //司机所在路线的下标
                mRouteIndex = (int) event.obj1;
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onDutyEvent(DutyEvent event) {
        if (event.type == DutyEvent.FEMALE_FORBIDDEN_NIGHT) {
            String msg = (String) event.obj1;
            if (AppManager.getInstance().currentActivity() instanceof PriceCheckActivity) {
                SoundUtils.getInstance().stop();
            }
            CompositeSubscription compositeDisposable = new CompositeSubscription();
            if (AppManager.getInstance().currentActivity() instanceof BaseActivity) {
                compositeDisposable.add(Observable.interval(100, TimeUnit.MILLISECONDS)
                        .compose(RxUtil.applySchedulers())
                        .subscribe(data -> {
                            if (AppManager.getInstance().currentActivity() instanceof PriceCheckActivity) {
                                SoundUtils.getInstance().stop();
                                return;
                            }

                            ((BaseActivity) AppManager.getInstance().currentActivity()).showBaseTipDialog(msg);

                            if (!TextUtils.isEmpty((String) event.obj2)) {
                                SpeechUtil.stop();
                                SpeechUtil.speech((String) event.obj2);
                            }
                            compositeDisposable.clear();

                        }, ex -> {
                        }));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGaoDeEvent(GaoDeServiceEvent event) {
        switch (event.type) {
            case GaoDeServiceEvent.isInService:
                BaseActivity baseActivity = (BaseActivity) AppManager.getInstance().currentActivity();
                baseActivity.showGaoDeInService();
                SpeechUtil.speech(this, getString(R.string.gaode_in_service));
                break;
            case GaoDeServiceEvent.isNotInService:
                SpeechUtil.speech(this, getString(R.string.gaode_not_in_service));
                BaseActivity baseActivity2 = (BaseActivity) AppManager.getInstance().currentActivity();
                baseActivity2.showGaoDeNotInService();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAwakeEvent(AwakenEvent event) {
        if (event.type == AwakenEvent.AWAKEN_EVENT) {
            try {
                String playVoice = (String) event.obj1;
                SpeechUtil.speech(this, playVoice);
                /////判断在前台的话弹出弹框
                if (BackgroundUtil.getAppStatus(this) == BackgroundUtil.RECEPTION) {
                    BaseActivity baseActivity = (BaseActivity) AppManager.getInstance().currentActivity();
                    baseActivity.showAwake(playVoice);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 真正开启长链接
     */
    private void connect() {
        disconnect(); //在建立新的长连接前，彻底关闭旧的长连接

        Timber.e("执行connect～");
        try {
            mClient = ISocketFactory.createClient(this, wsUrl);
            mClient.connectSocket(); // 开启长连接
        } catch (Exception e) {
            Timber.e("启动长连接 出现异常！");
            e.printStackTrace();
        }
    }

    /**
     * 主动关闭长链接
     */
    private void disconnect() {
        Timber.e("执行disconnect～");

        try {
            if (mClient != null) mClient.closeSocket();
        } catch (Exception e) {
            Timber.e("断开长连接 出现异常！");
            e.printStackTrace();
        }
        mClient = null;
    }

    /**
     * 定时操作
     * 上传位置信息或发送心跳包
     */
    private int timerUploadOperation() {
        UploadLocationMessage locationMessage = OrderManager.instance().getUploadLocationMessage(
                this, mDutyRepository, mDispatchRepository, mUserRepository);
        /////////不论如何都发送心跳包
        sendHeartBeatMessage(); //发送心跳包
        if (locationMessage == null) {
            return BackgroundUtil.getAppStatus(this);
        } else {
            /////获取所有没有上传成功的数据点，然后传送给服务器
            if (!TextUtils.isEmpty(locationMessage.getOrderUuid())) {

                if (mHasUnConnection) {
                    List<UploadPointEntity> data = mUploadPointRepository.queryNoUploadPoints(locationMessage.getOrderUuid());
                    if (data != null && data.size() > 0) {
                        int pointsDataLimit = 100;
                        List<UploadPointEntity> newList = new ArrayList<>();
                        for (int i = 0; i < data.size(); i++) {//分批次处理
                            newList.add(data.get(i));
                            if (pointsDataLimit == newList.size() || i == data.size() - 1) {
                                sendLocationMessageListByDB(newList);
                                newList.clear();
                            }
                        }

                        ////将数据库里面对应的isUpload更改.断网期间的点只传一次
                        for (UploadPointEntity entity : data) {
                            String tempLocationUuid = entity.getLocationUuid();
                            if (!TextUtils.isEmpty(tempLocationUuid)) {
                                mUploadPointRepository.updatePoint(entity.getOrderUuid(), tempLocationUuid);
                            }
                        }
                    } else {
                        sendLocationMessage(locationMessage);
                        ///////行程中的时候往数据库录入数据
                        mUploadPointRepository.insertPoint(UploadPointEntity.upload2Entity(locationMessage));
                    }
                    mHasUnConnection = false;
                } else {
                    sendLocationMessage(locationMessage);
                    /////数据插入数据库
                    mUploadPointRepository.insertPoint(UploadPointEntity.upload2Entity(locationMessage));
                }

            } else {
                ////////////不存入数据库的发送
                sendLocationMessage(locationMessage);
            }
        }
        return locationMessage.getAppStatus();
    }

    /**
     * 发送消息
     *
     * @param json
     */
    private void sendAndaMessage(AndaMessage json) {
        try {
            mClient.sendMessage(json);
        } catch (Exception e) {
            Timber.e("发送消息失败！");
            e.printStackTrace();
        }
    }

    /**
     * 发送登录报文
     */
    public void sendLoginMessage() {
        String token = InfoUtils.get().getToken();
        Header header = getHeader(MessageType.LOGIN);
        Login body = new Login(
                Login.FROM_DRIVER,
                token,
                DeviceUtil.getOsName(),
                DeviceUtil.getOsVersion(),
                DeviceUtil.getAppVersion(),
                DeviceUtil.getDeviceToken(this));
        AndaMessage loginMessage = new AndaMessage(header, body);
        sendAndaMessage(loginMessage);
    }

    /**
     * 发送心跳报文
     */
    public void sendHeartBeatMessage() {
        Header header = getHeader(MessageType.HEART_BEAT);
        HeartBeat body = new HeartBeat();
        AndaMessage heartBeatMessage = new AndaMessage(header, body);
        sendAndaMessage(heartBeatMessage);
    }

    /**
     * 发送地址报文
     */
    public void sendLocationMessage(UploadLocationMessage uploadLocationMessage) {
        if (uploadLocationMessage == null) return;
        Header header = getHeader(MessageType.UPLOAD_POSITION);
        UploadPosition position = msg2UploadPostion(uploadLocationMessage);
        if (position != null && (OrderManager.instance().getSubStatus() == OrderStatus.WAIT_ARRIVE_ORIGIN
                || OrderManager.instance().getSubStatus() == OrderStatus.DEPART)) {
            position.setRouteUid(mRouteId);
            position.setRouteIndex(mRouteIndex);
            position.setRouteRemainDistance(mRetainDistance);
            position.setRouteRemainTime(mRetainTime);
        }
        AndaMessage msg = new AndaMessage(header, UploadPositionList.createFrom(position)); //修改为List
        sendAndaMessage(msg);
    }

    /**
     * 发送之前上传失败的集合点新（从数据库里面获得的断网数据）
     * 直接将数据库里面的数据转化为要上传的数据，减少列表循环
     */
    public void sendLocationMessageListByDB(List<UploadPointEntity> entities) {
        if (entities == null) return;
        Header header = getHeader(MessageType.UPLOAD_POSITION);
        List<UploadPosition> uploadPositions = new ArrayList<>();

        for (UploadPointEntity entity : entities) {
            UploadPosition position = entity2UploadPotion(entity);
            uploadPositions.add(position);
        }
        AndaMessage msg = new AndaMessage(header, new UploadPositionList(uploadPositions));
        sendAndaMessage(msg);
    }

    private UploadPosition entity2UploadPotion(UploadPointEntity entity) {
        UploadPosition position = new UploadPosition();
        position.setAdcode(entity.getAdcode());
        position.setLocationUuid(entity.getLocationUuid());
        position.setDriverUuid(entity.getDriverUuid());
        position.setVehicleUuid(entity.getVehicleUuid());
        position.setVehLvUuid(entity.getVehLvUuid());
        position.setOrderUuid(entity.getOrderUuid());
        position.setPassengerUuid(entity.getPassengerUuid());
        position.setDistance(entity.getDistance());
        position.setLat(String.valueOf(NumberUtil.round(entity.getLat(), 6)));
        position.setLng(String.valueOf(NumberUtil.round(entity.getLng(), 6)));
        position.setAngle(entity.getAngle());
        position.setAppid(entity.getAppid());
        position.setMileage(entity.getMileage());
        position.setUploadTime(System.currentTimeMillis()); //uploadTime设置为当前时间
        position.setOrderStatus(entity.getOrderStatus());
        position.setSpeed(entity.getSpeed());
        position.setIsListen(entity.getIsListen());
        position.setDepend(entity.getDepend());
        position.setAppStatus(entity.getAppStatus());
        position.setDispatchUuid(entity.getDispatchUuid());
        position.setIsNavigation(entity.getIsNavigation()); //位置点为 导航点 or 定位点
        if (entity.getIsListen() != Integer.parseInt(DutyStatus.OFF_DUTY)) {  //出车状态下 传听单类型
            position.setRemindType(entity.getRemindType());
        }
        return position;
    }

    private UploadPosition msg2UploadPostion(UploadLocationMessage uploadLocationMessage) {
        UploadPosition position = new UploadPosition();
        position.setAdcode(uploadLocationMessage.getAdcode());
        position.setLocationUuid(uploadLocationMessage.getLocationUuid());
        position.setDriverUuid(uploadLocationMessage.getDriverUuid());
        position.setVehicleUuid(uploadLocationMessage.getVehicleUuid());
        position.setVehLvUuid(uploadLocationMessage.getVehLvUuid());
        position.setOrderUuid(uploadLocationMessage.getOrderUuid());
        position.setPassengerUuid(uploadLocationMessage.getPassengerUuid());
        position.setDistance(uploadLocationMessage.getDistance());
        position.setLat(String.valueOf(NumberUtil.round(uploadLocationMessage.getLat(), 6)));
        position.setLng(String.valueOf(NumberUtil.round(uploadLocationMessage.getLng(), 6)));
        position.setAngle(uploadLocationMessage.getAngle());
        position.setAppid(uploadLocationMessage.getAppid());
        position.setMileage(uploadLocationMessage.getMileage());
        position.setUploadTime(System.currentTimeMillis()); //uploadTime设置为当前时间
        position.setOrderStatus(uploadLocationMessage.getOrderStatus());
        position.setSpeed(uploadLocationMessage.getSpeed());
        position.setIsListen(uploadLocationMessage.getIsListen());
        position.setDepend(uploadLocationMessage.getDepend());
        position.setAppStatus(uploadLocationMessage.getAppStatus());
        position.setDispatchUuid(uploadLocationMessage.getDispatchUuid());
        position.setIsNavigation(uploadLocationMessage.getIsNavigation()); //位置点为 导航点 or 定位点
        if (uploadLocationMessage.getIsListen() != Integer.parseInt(DutyStatus.OFF_DUTY)) {  //出车状态下 传听单类型
            position.setRemindType(uploadLocationMessage.getRemindType());
        }
        return position;
    }

    /**
     * 获取最近一次上传的位置（和订单有关）
     */
    public void sendGetLocationOrder(String orderUuid) {
        if (TextUtils.isEmpty(orderUuid)) return;
        Header header = getHeader(MessageType.GET_POSITION);
        GetPosition body = new GetPosition(GetPosition.CONDITION_ORDER, orderUuid);
        AndaMessage message = new AndaMessage(header, body);
        sendAndaMessage(message);
    }

    /**
     * 发送推送反馈报文
     *
     * @param pushUuid
     */
    public void sendPushResponseMessage(String pushUuid) {
        Header header = getHeader(MessageType.RESP_PUSH);
        RespPush body = new RespPush(pushUuid);
        AndaMessage pushResponseMessage = new AndaMessage(header, body);
        sendAndaMessage(pushResponseMessage);
    }

    /**
     * 发送路径点
     *
     * @param points
     */
    public void sendRoutePoints(String points, long routeId) {
        Header header = getHeader(MessageType.UPLOAD_ROUTE);
        String orderUuid = OrderManager.instance().getUuid();
        int subStatus = OrderManager.instance().getSubStatus();
        long currentTime = System.currentTimeMillis();
        UploadRoute body = new UploadRoute(UploadRoute.FROM_DRIVER, orderUuid, subStatus, routeId, currentTime, points, mRouteIndex);
        AndaMessage pointMessage = new AndaMessage(header, body);
        sendAndaMessage(pointMessage);
    }

    /************************************
     * 其它操作
     ************************************/

    /**
     * 处理长连接的登录
     */
    public void dealWithLoginAction() {
        mUserRepository.getUserInfo()
                .compose(RxUtil.applySchedulers())
                .subscribe(entity -> {
                    //缓存用户信息
                    InfoUtils.get().setEntity(entity);
                    //发送登录报文
                    sendLoginMessage();
                }, ex -> {
                });
    }

    /**
     * 保存消息 并发送通知
     *
     * @param content
     */
    public void saveMessage(String content, String messageId) {
        if (TextUtils.isEmpty(messageId)) return;
        HxMessageEntity entity = JSON.parseObject(content, HxMessageEntity.class);
        entity.setUuid(messageId); //保存messageId
        mMessageRepository.saveMessage(entity)
                .subscribe(bool -> {
                    Timber.d(MessageFormat.format("-----> 保存消息成功：{0}", bool));
                    StringBuilder str = new StringBuilder(); //需要播报的内容
                    if (entity != null) {
                        str.append(entity.getTypeStr());
                        str.append(",");
                        str.append(TextUtils.isEmpty(entity.getReport())
                                ? entity.getContent() //如果report为null或""，使用content
                                : entity.getReport()); //使用report
                    }
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.NEW, str.toString()));
                }, ex -> Timber.e("-----> 保存消息出现异常"));
    }

    /**
     * 获取Header
     *
     * @param messageType
     * @return
     */
    public Header getHeader(int messageType) {
        String clientUuid = InfoUtils.get().getClientUuid();
        if (TextUtils.isEmpty(clientUuid)) { //如果为空，则从SP中获取
            clientUuid = mSP.getString(IConstants.USER_UUID);
        }
        return new Header(messageType, AppConfig.ANDA_APPKEY, clientUuid);
    }

    /**
     * 设置监听
     *
     * @param listener
     */
    public void setSocketListener(ISocketListener listener) {
        if (mClient != null) mClient.setSocketListener(listener);
    }

    /* ***** 20170727 将SocketService设置为前台Service ***** */

    private final static int NOTIFICATION_ID = 1001; //消息id
    private final static String CHANNEL_ID = "SOCKET_SERVICE";

    public void startForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Notification notification =
                    new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setContentTitle(getText(R.string.notification_title))
                            .setContentText(getText(R.string.notification_message))
                            .setSmallIcon(R.mipmap.hxyc_ic_launcher)
                            .setContentIntent(pendingIntent)
                            .build();
            startForeground(NOTIFICATION_ID, notification);
        } else {
            Notification.Builder builder = new Notification.Builder(getApplicationContext());
            startForeground(NOTIFICATION_ID, builder.build());
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.socket_channel_name);
            String description = getString(R.string.socket_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void stopForeground() {
        stopForeground(true);
    }

    /* ***** 2020 0427追加，获取传点频率 ***** */
    public static int getInterval() {
        SysConfigEntity sysConfigEntity = SysConfigUtils.get().getSysConfig();
        if (sysConfigEntity != null && !TextUtils.isEmpty(sysConfigEntity.getDrvPointInterval())) {
            int interval = Integer.parseInt(sysConfigEntity.getDrvPointInterval());
            if (interval > 0) {
                return interval * 1000;
            }
        }
        return INTERVAL;
    }

}
