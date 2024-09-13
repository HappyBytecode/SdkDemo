package anda.travel.driver.auth;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.hjq.toast.ToastUtils;
import com.luck.picture.lib.app.IApp;
import com.luck.picture.lib.app.PictureAppMaster;
import com.luck.picture.lib.engine.PictureSelectorEngine;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import anda.travel.driver.BuildConfig;
import anda.travel.driver.api.ApiConfig;
import anda.travel.driver.baselibrary.network.RequestError;
import anda.travel.driver.baselibrary.network.RetrofitRequestTool;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.baselibrary.utils.ToastUtil;
import anda.travel.driver.common.AppConfig;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.common.dagger.AppModule;
import anda.travel.driver.common.dagger.DaggerAppComponent;
import anda.travel.driver.config.DutyStatus;
import anda.travel.driver.config.HxErrorCode;
import anda.travel.driver.config.HxHomeStatus;
import anda.travel.driver.data.duty.DutyRepository;
import anda.travel.driver.data.entity.DriverEntity;
import anda.travel.driver.data.order.OrderRepository;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.event.DutyEvent;
import anda.travel.driver.module.launch.LaunchActivity;
import anda.travel.driver.module.main.MainActivity;
import anda.travel.driver.module.vo.OrderVO;
import anda.travel.driver.module.vo.UserInfoVO;
import anda.travel.driver.socket.SocketEvent;
import anda.travel.driver.socket.SocketService;
import anda.travel.driver.sound.SoundUtils;
import anda.travel.driver.util.BaseTaskSwitch;
import anda.travel.driver.util.CrashHandler;
import anda.travel.driver.util.EnvFactory;
import anda.travel.driver.util.Navigate;
import anda.travel.driver.util.OssManager;
import anda.travel.driver.pictureselect.PictureSelectorEngineImp;
import anda.travel.driver.util.SpeechUtil;
import anda.travel.driver.util.SysConfigUtils;
import ren.yale.android.cachewebviewlib.WebViewCacheInterceptor;
import ren.yale.android.cachewebviewlib.WebViewCacheInterceptorInst;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static anda.travel.driver.config.OrderStatus.ORDER_MAIN_STATUS_DOING;
import static android.content.Context.ACTIVITY_SERVICE;

/**
 * @author Lenovo
 */
public class HxClientManager {

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }

    private static AppComponent sAppComponent;
    @Inject
    CrashHandler crashHandler;
    public Application application;

    private String baiDuVoiceAppId;
    private String baiDuVoiceAppKey;
    private String baiDuVoiceSecretKey;

    public String getBaiDuVoiceAppId() {
        return baiDuVoiceAppId;
    }

    public String getBaiDuVoiceAppKey() {
        return baiDuVoiceAppKey;
    }

    public String getBaiDuVoiceSecretKey() {
        return baiDuVoiceSecretKey;
    }

    private CompositeSubscription mDisposable = new CompositeSubscription();

    @Inject
    UserRepository mUserRepository;
    @Inject
    OrderRepository mOrderRepository;
    @Inject
    DutyRepository mDutyRepository;


    /**
     * 回调相关
     */
    private OrderStatusListener orderStatusListener = null;

    public void setOrderStatusListener(OrderStatusListener orderStatusListener) {
        this.orderStatusListener = orderStatusListener;
    }

    private static class InstanceHolder {
        private static final HxClientManager INSTANCE = new HxClientManager();
    }

    private HxClientManager() {
    }

    public static HxClientManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 在init之前调用
     */
    public void setBaiDuVoiceKey(String baiDuVoiceAppId, String baiDuVoiceAppKey
            , String baiDuVoiceSecretKey) {
        this.baiDuVoiceAppId = baiDuVoiceAppId;
        this.baiDuVoiceAppKey = baiDuVoiceAppKey;
        this.baiDuVoiceSecretKey = baiDuVoiceSecretKey;
    }

    public void init(Application application) {
        this.application = application;
        initConfig(); //初始化参数
        sAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(application))
                .build();
        sAppComponent.inject(this);
        crashHandler.init();   //初始化崩溃处理
        boolean isMainProcess = application.getApplicationContext().getPackageName().equals
                (getCurrentProcessName());
        //防止多次初始化 影响性能
        if (isMainProcess) {
            //初始化配置信息工具类
            SysConfigUtils.init(application);
            // 初始化实人认证 SDK
            lazyLoad();
        }

        /**
         * 监听前后台切换
         */
        BaseTaskSwitch.init(application).setOnTaskSwitchListener(new BaseTaskSwitch.OnTaskSwitchListener() {
            @Override
            public void onTaskSwitchToForeground() {
                //Timber.e("应用切换到前台");
                EventBus.getDefault().post(new DutyEvent(DutyEvent.FOREGROUND));
            }

            @Override
            public void onTaskSwitchToBackground() {
                EventBus.getDefault().post(new DutyEvent(DutyEvent.BACKGROUND));
            }

            @Override
            public void onApplicationDestory() {

            }
        });
    }

    /**
     * 采用懒加载的方式减少主线程阻塞
     */
    private void lazyLoad() {
        Executors.newSingleThreadExecutor().execute(() -> {
            //初始化LitePal
            LitePal.initialize(application);
            initSound();

            PictureAppMaster.getInstance().setApp(new IApp() {
                @Override
                public Context getAppContext() {
                    return application.getApplicationContext();
                }

                @Override
                public PictureSelectorEngine getPictureSelectorEngine() {
                    return new PictureSelectorEngineImp();
                }
            });
            try {
                OssManager.INSTANCE.initOss(application);
            } catch (Exception e) {

            }
        });
    }

    private void initConfig() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        ToastUtils.init(application);
        //初始化百度语音
        SpeechUtil.init(application);
        //初始化应用参数
        AppConfig.initConfig(
                application.getResources(),
                EnvFactory.INSTANCE.createEnv(EnvFactory.ENV_RELEASE)
        );
        ApiConfig.setHost(AppConfig.HOST);
        RetrofitRequestTool.setKey(AppConfig.REQUEST_KEY);
        RetrofitRequestTool.setAppid(AppConfig.ANDA_APPKEY);

        WebViewCacheInterceptor.Builder builder = new WebViewCacheInterceptor.Builder(application);
        builder.setCacheSize(1024 * 1024 * 50);     // 设置缓存大小不超过50M
        WebViewCacheInterceptorInst.getInstance().init(builder);
    }

    /**
     * 初始化SoundService时，需避免被初始化多次
     */
    private void initSound() {
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果APP启用了远程的service，此application:onCreate会被调用2次
        // 为了防止被初始化2次，加此判断会保证被初始化1次
        // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回
        if (processAppName == null || !processAppName.equalsIgnoreCase(application.getPackageName())) {
            Timber.e("-----> enter the service process!");
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }
        SoundUtils.getInstance().init(application);
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) application.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = application.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    /**
     * 获取当前进程名
     */
    private String getCurrentProcessName() {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) application.getApplicationContext().getSystemService
                (ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;
            }
        }
        return processName;
    }

    public void startHxDriver() {
        Intent intent = new Intent(application.getApplicationContext(), LaunchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        application.getApplicationContext()
                .startActivity(intent);
    }

    public void register(HashMap<String, String> params, RegisterListener registerListener) {
        mDisposable.add(mUserRepository.registerZQ(params)
                .compose(RxUtil.applySchedulers())
                .subscribe(new Action1<UserInfoVO>() {
                    @Override
                    public void call(UserInfoVO userInfoVO) {
                        DriverEntity driverEntity = userInfoVO.driver;
                        driverEntity.identify = userInfoVO.identify;
                        mUserRepository.saveAccount(driverEntity.mobile);
                        mUserRepository.loginSuccess(driverEntity);
                        registerListener.registerSuccess();
                        startHxDriver();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable ex) {
                        if (ex instanceof RequestError) {
                            RequestError error = ((RequestError) ex);
                            registerListener.registerFail(error.getReturnCode(), error.getMsg());
                            return;
                        }
                        registerListener.registerFail(HxErrorCode.ERROR_UNKNOWN, ex.getMessage());
                    }
                })
        );
    }

    public void loginAuth(HashMap<String, String> params, LoginAuthListener loginAuthListener) {
        mDisposable.add(mUserRepository.loginAuthZQ(params)
                .compose(RxUtil.applySchedulers())
                .subscribe(new Action1<UserInfoVO>() {
                    @Override
                    public void call(UserInfoVO userInfoVO) {
                        DriverEntity driverEntity = userInfoVO.driver;
                        driverEntity.identify = userInfoVO.identify;
                        mUserRepository.saveAccount(driverEntity.mobile);
                        mUserRepository.loginSuccess(driverEntity);
                        loginAuthListener.loginAuthSuccess();
                        startHxDriver();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable ex) {
                        if (ex instanceof RequestError) {
                            RequestError error = ((RequestError) ex);
                            loginAuthListener.loginAuthFail(error.getReturnCode(), error.getMsg());
                            return;
                        }
                        loginAuthListener.loginAuthFail(HxErrorCode.ERROR_UNKNOWN, ex.getMessage());
                    }
                })
        );
    }

    public void loginOut() {
        mDisposable.add(mUserRepository.reqLogout()
                .compose(RxUtil.applySchedulers())
                .subscribe(s -> closeSocket()
                        , Timber::d));
    }

    public void offDutyAndLoginOut() {
        mDisposable.add(mDutyRepository.reqOffDuty(false)
                .compose(RxUtil.applySchedulers())
                .subscribe(s -> {
                    mDutyRepository.setIsOnDuty(false);
                    EventBus.getDefault().post(new SocketEvent(SocketEvent.UPLOAD_LOCATION_RIGHT_NOW)); //立即上传司机位置
                    mDisposable.add(mUserRepository.reqLogout()
                            .compose(RxUtil.applySchedulers())
                            .subscribe(s1 -> closeSocket()
                                    , Timber::d));
                }, Timber::d));
    }

    public void reqOrderStatus(OrderStatusListener orderStatusListener) {
        mDisposable.add(mOrderRepository.reqHomeStatus()
                .compose(RxUtil.applySchedulers())
                .subscribe(entity -> {
                    if (entity == null
                            || entity.status == null) {
                        return;
                    }
                    switch (entity.status) {
                        case HxHomeStatus.DRV_HOMEPAGE_STATUS_DOGIN:
                            //提示"有进行中订单"
                        case HxHomeStatus.DRV_HOMEPAGE_STATUS_TIME_OUT:
                            //提示"有预约订单已超过出发时间"
                        case HxHomeStatus.DRV_HOMEPAGE_STATUS_APPO:
                            //提示"有预约订单即将开始"
                            if (!TextUtils.isEmpty(entity.orderUuid)) {
                                orderStatusListener.orderStatusObtain(true
                                        , entity.orderUuid);
                            }
                            break;
                        case HxHomeStatus.DRV_HOMEPAGE_STATUS_NO_ORDER:
                        default:
                            orderStatusListener.orderStatusObtain(false, "");
                            break;
                    }
                }, throwable -> {
                    orderStatusListener.orderStatusObtain(false, "");
                    Timber.d(throwable);
                }));
    }

    public void openOrderByStatus(String orderUuid) {
        mDisposable.add(mOrderRepository.reqOrderDetail(orderUuid)
                .map(OrderVO::createFrom)
                .compose(RxUtil.applySchedulers())
                .subscribe(vo -> {
                    if (vo.canPickUp == null) {
                        vo.canPickUp = 0;
                    }
                    if (mDutyRepository.getIsOnDuty() == DutyStatus.OFF_DUTY_INT) {
                        if (ORDER_MAIN_STATUS_DOING == vo.mainStatus || vo.canPickUp == 1) {
                            ToastUtil.getInstance().toast("当前是收车状态，请出车后到订单页开始行程");
                            MainActivity.actionStartFromService(SocketService.getInstance()); //启动首页
                        } else {
                            Navigate.openOrderByStatusFromApp(application.getApplicationContext(), vo);
                        }
                    } else {
                        Navigate.openOrderByStatusFromApp(application.getApplicationContext(), vo);
                    }
                }, Timber::d));
    }

    public void startSocket() {
        SocketService.startService(application.getApplicationContext(), () -> Timber.d("onReady 触发！"));
    }

    public void closeSocket() {
        if (SocketService.getInstance() != null) {
            SocketService.getInstance().stopSelf(); //关闭Service
        }
    }

}  