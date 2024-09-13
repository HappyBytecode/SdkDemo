package anda.travel.driver.module.amap;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.CustomMapStyleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.utils.ToastUtil;
import anda.travel.driver.common.BaseFragment;
import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.data.entity.SysConfigEntity;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.event.MapEvent;
import anda.travel.driver.module.amap.assist.DrivingRouteOverlay;
import anda.travel.driver.module.amap.assist.MapUtils;
import anda.travel.driver.util.SysConfigUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * 功能描述：用于"路径规划"的地图片段
 * <p>
 */
public class AMapFragment extends BaseFragment implements AMapContract.View, RouteSearch.OnRouteSearchListener {

    public static AMapFragment newInstance() {
        return new AMapFragment();
    }

    @BindView(R2.id.map_view)
    TextureMapView mMapView;

    private final static int DefaultPadding = 80; //地图绘制点或悬浮物，距离边的距离（单位：像素）
    private int top;
    private int bottom;
    private LatLng onlyPoint; //只显示单独一个点

    private AMap mAMap; //地图对象
    private CustomMapStyleOptions mMapStyleOptions;
    private boolean mIsMapLoaded;
    private boolean mIsCalculated; //是否执行了路径规划
    private LatLonPoint mStartPoint;//起点
    private LatLonPoint mEndPoint;//终点
    private int routeId;//乘客选择的路线id

    private RouteSearch mRouteSearch; //路径规划对象
    private DriveRouteResult mDriveRouteResult; //路径规划的结果

    @Inject
    UserRepository mUserRepository;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.hxyc_fragment_amap, container, false);
        ButterKnife.bind(this, mView);
        HxClientManager.getAppComponent().inject(this); //依赖注入
        initMap(savedInstanceState);
        initRoute();
        EventBus.getDefault().register(this);
        return mView;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMapEvent(MapEvent mapEvent) {
        switch (mapEvent.type) {
            case MapEvent.MAP_CALCULATE_ROUTE: //路径规划
            case MapEvent.MAP_RESET_ROUTE: //重置路径规划
                if (mapEvent.obj1 == null || mapEvent.obj2 == null) {
                    return;
                }
                LatLng origin = (LatLng) mapEvent.obj1;
                LatLng dest = (LatLng) mapEvent.obj2;
                mStartPoint = new LatLonPoint(origin.latitude, origin.longitude);
                mEndPoint = new LatLonPoint(dest.latitude, dest.longitude);
                routeId = null == mapEvent.obj3 ? 12 : (int) mapEvent.obj3;
                if (mapEvent.type == MapEvent.MAP_RESET_ROUTE) {
                    mIsCalculated = false; //将mIsCaculated设置为false，以便重新规划路径
                    //mAMap.clear(); //清空地图上的标记
                }
                calculateDriverRoute(); //规划路径
                break;
            case MapEvent.MAP_PADDING:
                if (mapEvent.obj1 == null || mapEvent.obj2 == null) {
                    return;
                }
                top = (int) mapEvent.obj1;
                bottom = (int) mapEvent.obj2;

                if (onlyPoint != null) {
                    pointAddToMap(); //只显示单个点
                } else {
                    addToMap(true); //刷新地图显示
                }
                break;
            case MapEvent.MAP_POINT:
                if (mapEvent.obj1 == null) return;
                onlyPoint = (LatLng) mapEvent.obj1;
                pointAddToMap(); //只显示单个点
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        /////////销毁地图资源
        mAMap = null;
        mMapView.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //初始化地图
    private void initMap(Bundle savedInstanceState) {
        mAMap = mMapView.getMap(); //获取地图对象
        if (mAMap == null) return;
        mMapStyleOptions = new CustomMapStyleOptions();
        mAMap.setOnMapLoadedListener(() -> { //监听地图是否加载完成
            LatLng center = mUserRepository.getLatLng();
            if (center != null) { //默认以司机当前位置为中心点
                mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 15));
            }
            mIsMapLoaded = true;
            addToMap(false);
        });
        setMapCustomStyleFile(requireActivity());
        mMapStyleOptions.setEnable(true);
        mAMap.setCustomMapStyle(mMapStyleOptions);
        mMapView.onCreate(savedInstanceState); //此方法必须重写，才能正常显示
        mAMap.getUiSettings().setZoomControlsEnabled(false); //隐藏缩放按键
    }

    //初始化路径规划
    private void initRoute() {
        mRouteSearch = new RouteSearch(getContext());
        mRouteSearch.setRouteSearchListener(this);
    }

    /**
     * 规划路径
     */
    private void calculateDriverRoute() {
        if (mStartPoint == null //无起点
                || mEndPoint == null //无终点
                || mIsCalculated) //已执行路径规划
            return; //不执行以下操作

        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartPoint, mEndPoint);
        int strategy = getNaviStrategy();
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, strategy,
                null, null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
        mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mIsCalculated = true; //避免重复执行
                    mDriveRouteResult = result;
                    addToMap(false); //将路径添加到地图上
                } else if (result.getPaths() == null) {
                    ToastUtil.getInstance().toast("对不起，没有搜索到相关数据！");
                }
            } else {
                ToastUtil.getInstance().toast("对不起，没有搜索到相关数据！");
            }
        } else {
            Timber.e(MessageFormat.format("errorCode = {0}", errorCode));
        }
    }

    /**
     * 将路径添加到地图上
     */
    private void addToMap(boolean usePadding) {
        if (!mIsMapLoaded || mDriveRouteResult == null) return;
        mAMap.clear(); // 清理地图上的所有覆盖物
        DrivePath drivePath;
        SysConfigEntity sysConfigEntity = SysConfigUtils.get().getSysConfig();
        List<DrivePath> pathList = mDriveRouteResult.getPaths();
        if (null != sysConfigEntity && sysConfigEntity.getOpenMultipleRouteSelection() == 1
                && routeId >= 12 && routeId <= 14) {
            drivePath = pathList.get(routeId - 12 < pathList.size() ? routeId - 12 : 0);
        } else {
            drivePath = pathList.get(0);
        }
        DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                getContext(), mAMap, drivePath,
                mDriveRouteResult.getStartPos(),
                mDriveRouteResult.getTargetPos(), null);
        drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
        drivingRouteOverlay.setIsColorfulline(false);//是否用颜色展示交通拥堵情况，默认true
        drivingRouteOverlay.removeFromMap();
        drivingRouteOverlay.addToMap();
        //drivingRouteOverlay.zoomToSpan();
        if (usePadding) {
            LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
            boundsBuilder.include(new LatLng(mStartPoint.getLatitude(), mStartPoint.getLongitude()));
            boundsBuilder.include(new LatLng(mEndPoint.getLatitude(), mEndPoint.getLongitude()));
            mMapView.getMap().animateCamera(CameraUpdateFactory.newLatLngBoundsRect(boundsBuilder.build(),
                    DefaultPadding, //左边距
                    DefaultPadding, //右边距
                    top + DefaultPadding, //上边距
                    bottom + DefaultPadding), //下边距
                    200, null);
        } else {
            List<LatLng> points = new ArrayList<>();
            points.add(new LatLng(mStartPoint.getLatitude(), mStartPoint.getLongitude()));
            points.add(new LatLng(mEndPoint.getLatitude(), mEndPoint.getLongitude()));
            zoomToSpan(mAMap, points);
        }
    }

    /**
     * 将点添加到地图上
     */
    private void pointAddToMap() {
        if (onlyPoint == null) return;
        MapUtils.setOriginMarker(requireContext(), onlyPoint, mAMap);
        mMapView.getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(onlyPoint, 15), //下边距
                200, null);
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

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

    private void setMapCustomStyleFile(Context context) {
        String styleName = "amap/" + "style2.data";
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(styleName);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            if (mMapStyleOptions != null) {
                // 设置自定义样式
                mMapStyleOptions.setStyleData(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 缩放移动地图，保证所有自定义marker在可视范围中。
     */
    public static void zoomToSpan(AMap aMap, List<LatLng> latLngs) {
        if (latLngs != null && latLngs.size() > 0) {
            if (aMap == null) {
                return;
            }
            LatLngBounds bounds = getLatLngBounds(latLngs);
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        }
    }

    /**
     * 根据自定义内容获取缩放bounds
     */
    private static LatLngBounds getLatLngBounds(List<LatLng> latLngs) {
        LatLngBounds.Builder b = LatLngBounds.builder();
        for (int i = 0; i < latLngs.size(); i++) {
            LatLng p = latLngs.get(i);
            b.include(p);
        }
        return b.build();
    }
}
