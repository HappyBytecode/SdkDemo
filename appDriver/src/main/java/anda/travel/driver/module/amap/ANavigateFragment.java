package anda.travel.driver.module.amap;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSONArray;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.utils.SpatialRelationUtil;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapNaviLink;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviStep;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.BuildConfig;
import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.utils.SpannableWrap;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.data.entity.SysConfigEntity;
import anda.travel.driver.event.MapEvent;
import anda.travel.driver.module.amap.assist.MapUtils;
import anda.travel.driver.module.amap.dagger.ANavigateModule;
import anda.travel.driver.module.amap.dagger.DaggerANavigateComponent;
import anda.travel.driver.socket.SocketEvent;
import anda.travel.driver.util.GzipUtil;
import anda.travel.driver.util.SysConfigUtils;
import anda.travel.driver.util.TimeUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 功能描述：导航片段
 */
public class ANavigateFragment extends ANavigateBaseFragment implements ANavigateContract.View {

    @BindView(R2.id.navi_view)
    AMapNaviView mNaviView;

    private Marker mStartMarker;
    private Marker mEndMarker;
    private boolean mIsDispatch; // 20170811追加：是否是调度页（重要！）
    private final static int DefaultSpeed = IConstants.DefaultSpeed;
    private boolean mEmulator; //是否开启模拟导航

    private AMapNavi mAMapNavi;
    private boolean mIsNaviInited;
    private final List<NaviLatLng> sList = new ArrayList<>();
    private final List<NaviLatLng> eList = new ArrayList<>();

    private AMap mAMap;
    private boolean mShowTrafficLine; //显示交通状况

    private Marker mWaitMarker;
    private TextView mTvBubbleInfo;
    private boolean isShow;

    private int routeId;//乘客选择的路线id

    @Inject
    ANavigatePresenter mPresenter;

    public static ANavigateFragment newInstance(boolean isDispatch) {
        ANavigateFragment fragment = new ANavigateFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(IConstants.PARAMS, isDispatch);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static ANavigateFragment newInstance() {
        return newInstance(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.hxyc_fragment_anavigate, container, false);
        ButterKnife.bind(this, mView);
        if (getArguments() != null) {
            mIsDispatch = getArguments().getBoolean(IConstants.PARAMS);
        }
        initView(savedInstanceState);
        mPresenter.onCreate();
        return mView;
    }

    private void initView(Bundle savedInstanceState) {
        mAMapNavi = AMapNavi.getInstance(requireContext().getApplicationContext());
        mAMapNavi.addAMapNaviListener(this);
        mAMapNavi.setEmulatorNaviSpeed(DefaultSpeed);
        mNaviView.onCreate(savedInstanceState);
        mAMap = mNaviView.getMap();
        //设置导航的属性和ui
        MapUtils.setNaviOptions(requireContext(), mAMap, mNaviView);
        //mAmap.setMyLocationEnabled(true);
        mNaviView.setNaviMode(AMapNaviView.NORTH_UP_MODE);//正北模式
        mNaviView.setTrafficLine(mShowTrafficLine);//不显示交通状况
        mNaviView.setAMapNaviViewListener(this);
        showTrafficLine(true);

        mAMap.setInfoWindowAdapter(new AMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View view = LayoutInflater.from(requireActivity()).inflate(R.layout.hxyc_layout_bubble_map,
                        mNaviView, false);
                mTvBubbleInfo = view.findViewById(R.id.tv_bubble_info);
                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        DaggerANavigateComponent.builder().appComponent(getAppComponent())
                .aNavigateModule(new ANavigateModule(this)).build().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mNaviView.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mNaviView.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAMapNavi.removeAMapNaviListener(this);
        mNaviView.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!mIsDispatch) { //非调度页才执行
            mAMapNavi.stopNavi();
            mAMapNavi.destroy();
        }
    }

    @Override
    public void showTrafficLine(boolean showTrafficLine) {
        mShowTrafficLine = showTrafficLine;
        AMapNaviViewOptions options = mNaviView.getViewOptions();
        //options.setLaneInfoShow(mShowTrafficLine);
        options.setTrafficLine(mShowTrafficLine);//设置是否绘制显示交通路况的线路（彩虹线），拥堵-红色，畅通-绿色，缓慢-黄色，未知-蓝色。
        options.setTrafficInfoUpdateEnabled(mShowTrafficLine);//设置是否显示道路信息view
        mNaviView.setViewOptions(options);
    }

    @Override
    public void setMapPadding(int top, int bottom) {
    }

    @Override
    public void locate(LatLng center) {
        if (center == null) return;
        mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, MapUtils.DEFAULT_ZOOM_LEVEL));
        //mAmap.animateCamera(CameraUpdateFactory.changeLatLng(center));
    }

    @Override
    public void calculateRoute(LatLng origin, LatLng dest) {

    }

    @Override
    public void navigate(LatLng origin, LatLng dest, int routeId) {
        this.routeId = routeId;
        sList.clear();
        eList.clear();
        //导航起点
        //导航终点
        sList.add(new NaviLatLng(origin.latitude, origin.longitude));
        eList.add(new NaviLatLng(dest.latitude, dest.longitude));
        if (mIsNaviInited) onInitNaviSuccess(); //开启导航
    }

    @Override
    public void setOrderPoint(LatLng origin, LatLng dest) {
//        if (mOrderOrigin != null && mOrderDest != null) return; //只能设置一次起终点
        if (mStartMarker != null) {
            mStartMarker.remove();
        }
        if (mEndMarker != null) {
            mEndMarker.remove();
        }
        //订单起点
        //订单终点
        mStartMarker = MapUtils.setOriginMarker(requireContext(), origin, mAMap);
        mEndMarker = MapUtils.setEndMarker(requireContext(), dest, mAMap);
    }

    @Override
    public void postNaviInfo(NaviInfo naviInfo) {
        if (naviInfo == null) return;
        EventBus.getDefault().post(new MapEvent(MapEvent.VIEW_NaviInfoUpdate, naviInfo)); //通知其它界面，刷新距离和时间显示
        EventBus.getDefault().post(new SocketEvent(SocketEvent.VIEW_NAVI_INFO_UPDATE, naviInfo)); //通知SocketService获取最新导航信息
    }

    @Override
    public void emulator(boolean isEmulator) {
        mEmulator = isEmulator;
        if (mIsNaviInited) onInitNaviSuccess();
    }

    @Override
    public void showLateTime(long lateTime) {
        if (!isShow) {
            mWaitMarker = mAMap.addMarker(new MarkerOptions().
                    position(mPresenter.getCurrentLatLng()).icon(BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(
                            requireContext().getResources(), R.drawable.wait_current_location))));
            mWaitMarker.setObject("wait");
            mWaitMarker.showInfoWindow();
            isShow = true;
        }
        if (mTvBubbleInfo != null) {
            SpannableWrap.setText("已等待  ")
                    .textColor(ContextCompat.getColor(requireContext(), R.color.popup_item_un_choose))
                    .append(TimeUtils.getTime(lateTime))
                    .textColor(ContextCompat.getColor(requireContext(), R.color.popup_item_choose))
                    .into(mTvBubbleInfo);
        }
    }

    @Override
    public void hideLateTime() {
        isShow = false;
        if (mWaitMarker != null) {
            mWaitMarker.hideInfoWindow();
            mWaitMarker.remove();
        }
    }

    @Override
    public void onNaviViewLoaded() {
        super.onNaviViewLoaded();
        LatLng center = mPresenter.getCurrentLatLng();
        if (center != null) {
            mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 15));
        }
    }

    @Override
    public void onInitNaviSuccess() {
        super.onInitNaviSuccess();
        mIsNaviInited = true; //已初始化
        if (!mIsDispatch) { //非调度页才执行
            if (sList.isEmpty() || eList.isEmpty()) return;
            mAMapNavi.stopNavi(); //先停止导航
            EventBus.getDefault().post(new MapEvent(MapEvent.VIEW_RESET)); //通知其它界面，重置距离和时间显示
        }

//        /**
//         * 方法: int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute); 参数:
//         *
//         * @congestion 躲避拥堵
//         * @avoidhightspeed 不走高速
//         * @cost 避免收费
//         * @hightspeed 高速优先
//         * @multipleroute 多路径
//         *
//         *  说明: 以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
//         *  注意: 不走高速与高速优先不能同时为true 高速优先与避免收费不能同时为true
//         */
//        int strategy = 0;
//        try {
//            //再次强调，最后一个参数为true时代表多路径，否则代表单路径
//            strategy = mAMapNavi.strategyConvert(false, false, false, false, false);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        int strategy = getNaviStrategy();
        mAMapNavi.calculateDriveRoute(sList, eList, null, strategy);
    }

    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult result) {
        if (!mIsDispatch) { //非调度页才执行
            //EventBus.getDefault().post(new MapEvent(MapEvent.VIEW_ARRIVE_DEST, false)); //导航开始

            /* 只有测试版本，可以模拟导航 */
            int naviType = (mEmulator && BuildConfig.DEBUG) ? NaviType.EMULATOR : NaviType.GPS;
            SysConfigEntity sysConfigEntity = SysConfigUtils.get().getSysConfig();
            if (null != sysConfigEntity && sysConfigEntity.getOpenMultipleRouteSelection() == 1
                    && routeId >= 12 && routeId <= 14) {
                mAMapNavi.selectRouteId(routeId);
            }
            mAMapNavi.startNavi(naviType);
            uploadNaviPath();
        }
        super.onCalculateRouteSuccess(result);
    }

    private final List<LatLng> naviPathPoints = new ArrayList<>();

    /**
     * 上传规划成功后的导航路线
     */
    private void uploadNaviPath() {
        naviPathPoints.clear();
        AMapNaviPath naviPath = mAMapNavi.getNaviPath();
        if (naviPath == null) return;
        long routeId = naviPath.getPathid();
        // 遍历获取所有点集合转成json并gzip压缩和使用项目中的base64工具加密
        List<AMapNaviStep> steps = naviPath.getSteps();
        for (AMapNaviStep step : steps) {
            List<AMapNaviLink> links = step.getLinks();
            for (AMapNaviLink link : links) {
                List<NaviLatLng> coords = link.getCoords();
                for (NaviLatLng coord : coords) {
                    double latitude = coord.getLatitude();
                    double longitude = coord.getLongitude();
                    naviPathPoints.add(new LatLng(latitude, longitude));
                }
            }
        }

        String jsonStr = JSONArray.toJSONString(naviPathPoints);
        String base64Str = GzipUtil.encryptGZIP(jsonStr);
        EventBus.getDefault().post(new SocketEvent(SocketEvent.UPLOAD_ROUTE_POINTS, base64Str, routeId));
    }

    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult result) {
        super.onCalculateRouteFailure(result);
    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {
        super.onNaviInfoUpdate(naviInfo);
        postNaviInfo(naviInfo);
    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        //多路径算路成功回调
    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
        super.onLocationChange(aMapNaviLocation);
        if (aMapNaviLocation == null) return;
        EventBus.getDefault().post(new MapEvent(MapEvent.VIEW_LocationChange, aMapNaviLocation));
        calculateDriverIndex(aMapNaviLocation);
    }

    /**
     * 计算司机在路线上的下标位置
     */
    private void calculateDriverIndex(AMapNaviLocation aMapNaviLocation) {
        double curLat = aMapNaviLocation.getCoord().getLatitude();
        double curLon = aMapNaviLocation.getCoord().getLongitude();
        Pair<Integer, LatLng> carIndex = SpatialRelationUtil.calShortestDistancePoint(naviPathPoints, new LatLng(curLat, curLon));
        int index = carIndex.first;
        EventBus.getDefault().post(new SocketEvent(SocketEvent.UPLOAD_DRIVER_INDEX, index));

        // 第二种计算路径点的方法
        /*int index = 0;
        int carIndex = 0;
        for (int i = 0; i < naviPath.getSteps().size(); i++) {
            List<AMapNaviLink> links = naviPath.getSteps().get(i).getLinks();
            for (int j = 0; j < links.size(); j++) {
                List<NaviLatLng> coords = links.get(j).getCoords();
                for (int k = 0; k < coords.size(); k++) {
                    index ++;
                    if (aMapNaviLocation.getCurStepIndex() == i
                            && aMapNaviLocation.getCurLinkIndex() == j
                                && aMapNaviLocation.getCurPointIndex() == k) {
                        carIndex = index;
                        break;
                    }
                 }
            }
        }*/
    }

    @Override
    public void onArriveDestination() {
        super.onArriveDestination();
        //EventBus.getDefault().post(new MapEvent(MapEvent.VIEW_ARRIVE_DEST, true)); //导航到达目的地
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
    public void onReCalculateRouteForYaw() {
        //偏航后重新计算路线回调
        super.onReCalculateRouteForYaw();
        EventBus.getDefault().post(new MapEvent(MapEvent.VIEW_RECALCULATE));
    }
}
