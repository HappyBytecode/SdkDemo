package anda.travel.driver.data.dispatch;

import android.text.TextUtils;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;

import org.greenrobot.eventbus.EventBus;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import anda.travel.driver.BuildConfig;
import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.data.entity.LocationEntity;
import anda.travel.driver.data.entity.PointEntity;
import anda.travel.driver.data.order.OrderRepository;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.event.DispatchEvent;
import anda.travel.driver.module.amap.assist.CalculateUtils;
import anda.travel.driver.module.vo.DispatchVO;
import anda.travel.driver.util.SysConfigUtils;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.baselibrary.utils.TypeUtil;
import rx.Subscription;
import timber.log.Timber;

@Singleton
public class DispatchRepository implements DispatchSource, AMapNaviListener {

    private final static float MAX_SPEED = 50F; // 50 米每秒（也就是 180 千米每小时）
    private final static float MAX_ACCURACY = 100; //最大的进度误差

    private final UserRepository mUserRepository;
    private final OrderRepository mOrderRepository;

    @Inject
    public DispatchRepository(
            UserRepository userRepository,
            OrderRepository orderRepository) {
        mUserRepository = userRepository;
        mOrderRepository = orderRepository;
    }

    private boolean mDispatchEmulator; //是否开启模拟导航

    private AMapNavi mAMapNavi; //导航
    private boolean mIsNaviInited; //是否已初始化
    private final List<NaviLatLng> sList = new ArrayList<>();
    private final List<NaviLatLng> eList = new ArrayList<>();
    private Integer mRetainDistance; //剩余距离（单位：米）
    private Integer mRetainTime; //剩余时间（单位：分钟）
    private LatLng mCurrentLatLng; //导航返回的当前位置

    private DispatchVO mDispatchVO; //调度信息
    private LatLng mEndLatLng; //调度目标地点的坐标
    private double mLastTripDistance; //上次已行驶的调度里程（单位：米）
    private float mNaviTripDistance; //本次导航的调度里程（单位：米）

    private PointEntity mPreviousPoint;
    private boolean mIsCompleting;
    private boolean mIsDispatchDisplay;

    @Override
    public void setIsDispatchDisplay(boolean isDispatchDisplay) {
        mIsDispatchDisplay = isDispatchDisplay;
    }

    @Override
    public boolean getIsDispatchDisplay() {
        return mIsDispatchDisplay;
    }

    @Override
    public void createNavi() {
        if (mAMapNavi != null) return;
        resetParams(); //重置参数

        mAMapNavi = AMapNavi.getInstance(HxClientManager.getInstance().application.getApplicationContext());
        mAMapNavi.addAMapNaviListener(this);
        mAMapNavi.setEmulatorNaviSpeed(IConstants.DefaultSpeed);
    }

    @Override
    public void destoryNavi() {
        resetParams(); //重置参数

        if (mAMapNavi == null) return;
        mAMapNavi.removeAMapNaviListener(this);
        mAMapNavi.stopNavi();
        mAMapNavi.destroy();
        mAMapNavi = null;
    }

    private void resetParams() {
        mDispatchEmulator = false;
        mIsNaviInited = false;
        mRetainDistance = mRetainTime = null;
        mCurrentLatLng = null;
        mDispatchVO = null;
        mLastTripDistance = mNaviTripDistance = 0;
        sList.clear();
        eList.clear();
        mPreviousPoint = null;
        mIsCompleting = false;
        mIsDispatchDisplay = false;
        mEndLatLng = null;
    }

    @Override
    public void setDispatch(DispatchVO dispatchVO) {
        if (dispatchVO == null) {
            destoryNavi(); //销毁导航
        } else {
            createNavi(); //创建导航
            if (mDispatchVO != null && mDispatchVO.getUuid().equals(dispatchVO.getUuid())) {
                //无需重新导航
            } else {
                mLastTripDistance = TypeUtil.getValue(dispatchVO.actualTrip) * 1000;
                mNaviTripDistance = 0;
                if (dispatchVO.endLat != null
                        && dispatchVO.endLat != 0
                        && dispatchVO.endLng != null
                        && dispatchVO.endLng != 0) {
                    mEndLatLng = new LatLng(dispatchVO.endLat, dispatchVO.endLng);
                } else {
                    mEndLatLng = null;
                }
                LatLng current = mUserRepository.getLatLng();
                startNavi(new NaviLatLng(current.latitude, current.longitude),
                        new NaviLatLng(dispatchVO.endLat, dispatchVO.endLng));
            }
        }
        mDispatchVO = dispatchVO;
    }

    @Override
    public DispatchVO getDispatch() {
        return mDispatchVO;
    }

    @Override
    public void startNavi(NaviLatLng sLatlng, NaviLatLng eLatlng) {
        sList.clear();
        sList.add(sLatlng);
        eList.clear();
        eList.add(eLatlng);
        if (mIsNaviInited) onInitNaviSuccess(); //开启导航
    }

    @Override
    public Integer getRetainDistance() {
        Timber.e(MessageFormat.format("-----> mRetainDistance={0}", mRetainDistance));
        return mRetainDistance;
    }

    @Override
    public Integer getRetainTime() {
        Timber.e(MessageFormat.format("-----> mRetainTime={0}", mRetainTime));
        return mRetainTime;
    }

    @Override
    public LatLng getDispatchCurrentLatLng() {
        return mCurrentLatLng == null ? mUserRepository.getLatLng() : mCurrentLatLng;
    }

    @Override
    public boolean getDispatchEmulator() {
        return mDispatchEmulator;
    }

    @Override
    public void setDispatchEmulator(boolean dispatchEmulator) {
        if (BuildConfig.DEBUG) { //只有调试模式，才能设置
            mDispatchEmulator = dispatchEmulator;
            if (mIsNaviInited) onInitNaviSuccess();
        }
    }

    @Override
    public void addPoint(PointEntity point) {
        boolean isValid = point.getAccuracy() < MAX_ACCURACY; //是否为有效点
        if (mPreviousPoint != null) {
            long duration = point.getLoctime() - mPreviousPoint.getLoctime(); //时间(单位：毫秒)
            point.setDuration(duration);

            isValid = duration > 0;
            if (isValid) {
                float distance = CalculateUtils.calculateLineDistance(mPreviousPoint.getLatLng(), point.getLatLng()); //距离(单位米)
                point.setDistance(distance);

                float avgSpeed = distance * 1000 / duration; //计算出来的平均速度(m/s)
                point.setAvgSpeed(avgSpeed);

                isValid = avgSpeed < MAX_SPEED; //速度符合正确范围
                if (isValid) mNaviTripDistance += distance; //增加里程
                point.setTotalDistance(mNaviTripDistance);
            }
        }
        if (isValid) mPreviousPoint = point; //记录有效点

        point.setUuid(mDispatchVO.getUuid());
        point.setValid(isValid);
    }

    @Override
    public double queryTotalDistance() {
        double total = ((int) (mNaviTripDistance + mLastTripDistance)) * 1.0D / 1000;
        Timber.e(MessageFormat.format("-----> 调度里程：{0}", total));
        return total; //总里程(单位km)
    }

    private Subscription mSub;

    @Override
    public void dispatchComplete(String orderUuid) {
        if (mDispatchVO == null || mIsCompleting) return;
        mIsCompleting = true;
        Timber.e(MessageFormat.format("-----> 结束调度：orderUuid = {0}", orderUuid));

        HashMap<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(orderUuid)) {
            params.put("orderUuid", orderUuid);
        }
        params.put("dispatchUuid", mDispatchVO.getUuid());
        params.put("mileage", "" + queryTotalDistance());

        destoryNavi();
        mSub = mOrderRepository.dispatchComplete(params)
                .compose(RxUtil.applySchedulers())
                .subscribe(s -> {
                    mIsCompleting = false;

                    String content = TextUtils.isEmpty(orderUuid) ? "您已到达调度目的地，完成调度" : null;
                    EventBus.getDefault().post(new DispatchEvent(DispatchEvent.DISPATCH_Refresh, content));

                    unsubscribe();
                }, ex -> {
                    mIsCompleting = false;
                    unsubscribe();
                });
    }

    private void unsubscribe() {
        if (mSub != null) {
            mSub.unsubscribe();
            mSub = null;
        }
    }

    private int getNaviStrategy() {
        return SysConfigUtils.get().getNaviStrategy();
//        try {
//            MyConfig config = ParseUtils.getInstance().getMyConfig();
//            if (config != null
//                    && (!TextUtils.isEmpty(config.getNaviStrategy())))
//                return Integer.valueOf(config.getNaviStrategy());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return 0; //默认的导航策略
    }

    @Override
    public void onInitNaviSuccess() {
        mIsNaviInited = true; //已初始化
        if (sList.isEmpty() || eList.isEmpty()) return;

        mAMapNavi.stopNavi(); //先停止导航
        //EventBus.getDefault().post(new MapEvent(MapEvent.VIEW_RESET)); //通知其它界面，重置距离和时间显示
        int strategy = getNaviStrategy();
        mAMapNavi.calculateDriveRoute(sList, eList, null, strategy);
    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        /* 只有测试版本，可以模拟导航 */
        int naviType = (mDispatchEmulator && BuildConfig.DEBUG) ? NaviType.EMULATOR : NaviType.GPS;
        mAMapNavi.startNavi(naviType);
    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {
        if (naviInfo == null) return;

        mRetainDistance = naviInfo.getPathRetainDistance();
        mRetainTime = naviInfo.getPathRetainTime();
        Timber.d("-----> onNaviInfoUpdate：" +
                "\nmRetainDistance=" + mRetainDistance +
                "\nmRetainTime=" + mRetainTime);
        postEvent(); //发送通知消息
    }

    /**
     * 发送通知消息
     */
    private void postEvent() {
        if (mDispatchVO == null) return;
        EventBus.getDefault().post(
                new DispatchEvent(
                        DispatchEvent.DISPATCH_NaviInfoUpdate,
                        mDispatchVO.endAddress, //地址
                        mRetainDistance)); //距离
    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
        NaviLatLng coord = aMapNaviLocation.getCoord();
        if (coord == null || coord.getLatitude() == 0 || coord.getLongitude() == 0) {
            LocationEntity loc = mUserRepository.getCurrentLocation();
            if (loc != null) mCurrentLatLng = new LatLng(loc.lat, loc.lng);
        } else {
            mCurrentLatLng = new LatLng(coord.getLatitude(), coord.getLongitude());
        }
        if (mDispatchVO != null && mCurrentLatLng != null) {
            PointEntity point = new PointEntity();
            point.setLat(mCurrentLatLng.latitude);
            point.setLon(mCurrentLatLng.longitude);
            point.setBearing(aMapNaviLocation.getBearing());
            point.setSpeed(IConstants.DefaultSpeed); //传入的数据单位为：km/h
            point.setLoctime(System.currentTimeMillis());
            addPoint(point);
        }

        // 判断是否要结束导航（默认endRange=300）
        int endRange = (mDispatchVO != null && mDispatchVO.endRange != null && mDispatchVO.endRange > 0)
                ? mDispatchVO.endRange : 300;
        if ((mEndLatLng != null && mCurrentLatLng != null)
                ? CalculateUtils.calculateLineDistance(mCurrentLatLng, mEndLatLng) <= endRange
                : (mRetainDistance != null && mRetainDistance <= endRange)) {
            dispatchComplete(null);
        }
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {

    }

    @Override
    public void hideModeCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    public void onPlayRing(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {

    }

    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {

    }

    @Override
    public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {

    }

    @Override
    public void onGpsSignalWeak(boolean b) {

    }
}
