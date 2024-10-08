package anda.travel.driver.module.amap.navi;

import android.os.Bundle;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;

import java.util.ArrayList;
import java.util.List;

import anda.travel.driver.common.BaseActivityWithoutIconics;
import timber.log.Timber;

/**
 * 功能描述：
 */
public abstract class BaseNaviActivity extends BaseActivityWithoutIconics implements AMapNaviListener, AMapNaviViewListener {

    AMapNaviView mAMapNaviView;
    AMapNavi mAMapNavi;
    NaviLatLng mEndLatlng;
    NaviLatLng mStartLatlng;
    final List<NaviLatLng> sList = new ArrayList<>();
    final List<NaviLatLng> eList = new ArrayList<>();
    List<NaviLatLng> mWayPointList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAMapNaviView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAMapNaviView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAMapNaviView.onDestroy();
        //since 1.6.0 不再在naviview destroy的时候自动执行AMapNavi.stopNavi();请自行执行
//        mAMapNavi.stopNavi();
//        mAMapNavi.destroy();
//        mAMapNavi.removeAMapNaviListener(mTtsManager);
//        mTtsManager.destroy();
    }

    @Override
    public void onInitNaviFailure() {
        //ToastUtil.getInstance().toast("init navi Failed");
        //Toast.makeText(this, "init navi Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInitNaviSuccess() {
        //初始化成功
    }

    @Override
    public void onStartNavi(int type) {
        //开始导航回调
    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation location) {
        //当前位置回调
    }

    @Override
    public void onGetNavigationText(int type, String text) {
        //播报类型和播报文字回调
    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onEndEmulatorNavi() {
        //结束模拟导航
    }

    @Override
    public void onArriveDestination() {
        //到达目的地
    }

    @Override
    public void onCalculateRouteFailure(int errorInfo) {
        //路线计算失败
        Timber.e("--------------------------------------------");
        Timber.i("路线计算失败：错误码=" + errorInfo + ",Error Message= " + ErrorInfo.getError(errorInfo));
        Timber.i("错误码详细链接见：http://lbs.amap.com/api/android-navi-sdk/guide/tools/errorcode/");
        Timber.e("--------------------------------------------");
        //ToastUtil.getInstance().toast("errorInfo：" + errorInfo + ",Message：" + ErrorInfo.getError(errorInfo));
        //Toast.makeText(this, "errorInfo：" + errorInfo + ",Message：" + ErrorInfo.getError(errorInfo), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onReCalculateRouteForYaw() {
        //偏航后重新计算路线回调
    }

    @Override
    public void onReCalculateRouteForTrafficJam() {
        //拥堵后重新计算路线回调
    }

    @Override
    public void onArrivedWayPoint(int wayID) {
        //到达途径点
    }

    @Override
    public void onGpsOpenStatus(boolean enabled) {
        //GPS开关状态回调
    }

    @Override
    public void onNaviSetting() {
        //底部导航设置点击回调
    }

    @Override
    public void onNaviMapMode(int isLock) {
        //地图的模式，锁屏或锁车
    }

    @Override
    public void onNaviCancel() {
        finish();
    }

    @Override
    public void onNaviTurnClick() {
        //转弯view的点击回调
    }

    @Override
    public void onNextRoadClick() {
        //下一个道路View点击回调
    }

    @Override
    public void onScanViewButtonClick() {
        //全览按钮点击回调
    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviinfo) {
        //导航过程中的信息更新，请看NaviInfo的具体说明
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {
        //已过时
    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {
        //显示转弯回调
    }

    @Override
    public void hideCross() {
        //隐藏转弯回调
    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] laneInfos, byte[] laneBackgroundInfo, byte[] laneRecommendedInfo) {
        //显示车道信息
    }

    @Override
    public void hideLaneInfo() {
        //隐藏车道信息
    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {

    }

    @Override
    public void notifyParallelRoad(int i) {
//        if (i == 0) {
//            ToastUtil.getInstance().toast("当前在主辅路过渡");
//            //Toast.makeText(this, "当前在主辅路过渡", Toast.LENGTH_SHORT).show();
//            Log.d("wlx", "当前在主辅路过渡");
//            return;
//        }
//        if (i == 1) {
//            ToastUtil.getInstance().toast("当前在主路");
//            //Toast.makeText(this, "当前在主路", Toast.LENGTH_SHORT).show();
//            Log.d("wlx", "当前在主路");
//            return;
//        }
//        if (i == 2) {
//            ToastUtil.getInstance().toast("当前在辅路");
//            //Toast.makeText(this, "当前在辅路", Toast.LENGTH_SHORT).show();
//            Log.d("wlx", "当前在辅路");
//        }
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {
        //更新交通设施信息
    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {
        //更新巡航模式的统计信息
    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {
        //更新巡航模式的拥堵信息
    }

    @Override
    public void onPlayRing(int i) {

    }

    @Override
    public void onLockMap(boolean isLock) {
        //锁地图状态发生变化时回调
    }

    @Override
    public void onNaviViewLoaded() {
        Timber.d("导航页面加载成功");
        Timber.d("请不要使用AMapNaviView.getMap().setOnMapLoadedListener();会overwrite导航SDK内部画线逻辑");
    }

    @Override
    public boolean onNaviBackClick() {
        finish(); //关闭界面
        return true;
    }
}
