package anda.travel.driver.module.amap.assist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.amap.api.maps.AMap;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.model.RouteOverlayOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import anda.travel.driver.R;

/**
 * Created by Ling on 2016/10/15.
 */

public class MapUtils {

    public final static int DEFAULT_ZOOM_LEVEL = 15;
    private static Marker myLocationMarker;

    public static void setNaviOptions(Context context, AMap map, AMapNaviView aMapNaviView) {
        /*设置车标*/
        AMapNaviViewOptions options = aMapNaviView.getViewOptions();
        //options.setLockMapDelayed(24*60*60*100L);
        options.setTilt(0);
        options.setLayoutVisible(false);
        //6s后自动锁车
        options.setAutoLockCar(true);
        options.setCarBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_car));
        Bitmap transparentBitmap = ImageUtil.getTransparentBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_car), 0);
        options.setFourCornersBitmap(transparentBitmap);
        options.setStartPointBitmap(transparentBitmap);
        options.setEndPointBitmap(transparentBitmap);
        //关闭自动绘制路线（如果你想自行绘制路线的话，必须关闭！！！）
        options.setAutoDrawRoute(true);
        //主动隐藏蚯蚓线
        options.setLaneInfoShow(true);
        //options.setTrafficLine(true);//设置是否绘制显示交通路况的线路（彩虹线），拥堵-红色，畅通-绿色，缓慢-黄色，未知-蓝色。
        //options.setTrafficInfoUpdateEnabled(true);//设置是否显示道路信息view
        options.setRouteListButtonShow(false);
        options.setCompassEnabled(false);
        options.setTrafficBarEnabled(false);//设置路况光柱条是否显示（只适用于驾车导航，需要联网）
        options.setScreenAlwaysBright(true);//屏幕常亮
        options.setAutoChangeZoom(false);
        options.setZoom(DEFAULT_ZOOM_LEVEL);
        options.setCameraBubbleShow(false); //关闭路线上的摄像头气泡
        //hideArrow(context, options);

        RouteOverlayOptions routeOverlayOptions = new RouteOverlayOptions();
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrowtexture);
        Bitmap passBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrowtexture_grey);
        Bitmap transparent = BitmapFactory.decodeResource(context.getResources(), R.drawable.transparent);
        routeOverlayOptions.setArrowColor(Color.parseColor("#32CD32"));
        routeOverlayOptions.setArrowOnTrafficRoute(transparent);
        routeOverlayOptions.setSmoothTraffic(bitmap);
        routeOverlayOptions.setSlowTraffic(bitmap);
        routeOverlayOptions.setJamTraffic(bitmap);
        routeOverlayOptions.setUnknownTraffic(bitmap);
        routeOverlayOptions.setPassRoute(passBitmap);
        options.setRouteOverlayOptions(routeOverlayOptions);
        options.setAfterRouteAutoGray(true);    //已通过路线置灰
        aMapNaviView.setViewOptions(options);

        if (map == null) map = aMapNaviView.getMap();
        UiSettings settings = map.getUiSettings();
        settings.setRotateGesturesEnabled(false);
        settings.setTiltGesturesEnabled(false);
        settings.setCompassEnabled(false);
    }

//    public static void hideArrow(Context context, AMapNaviViewOptions options) {
//        //隐藏导航箭头
//        RouteOverlayOptions routeOverlayOptions = new RouteOverlayOptions();
//        routeOverlayOptions.setArrowOnTrafficRoute(
//                ImageUtil.getTransparentBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.custtexture_aolr), 0));
//        options.setRouteOverlayOptions(routeOverlayOptions);
//    }

    /**
     * 设置起点Marker
     */
    public static Marker setOriginMarker(Context context, LatLng latLng, AMap aMap) {
        return aMap.addMarker(new MarkerOptions().
                position(latLng).
                title("起点").
                icon(BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(
                                context.getResources(), R.drawable.map_icon_start))));
    }

    /**
     * 设置终点Marker
     */
    public static Marker setEndMarker(Context context, LatLng latLng, AMap aMap) {
        return aMap.addMarker(new MarkerOptions().
                position(latLng).
                title("终点").
                icon(BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(
                                context.getResources(), R.drawable.map_icon_end))));
    }

    /**
     * 设置终点Marker
     */
    public static void setMyLocationMarker(Context context, LatLng latLng, AMap aMap) {
        myLocationMarker = aMap.addMarker(new MarkerOptions().
                position(latLng).
                title("我的位置").
                icon(BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(
                                context.getResources(), R.drawable.icon_car))));
    }

    public static Marker getMyLocationMarker() {
        return myLocationMarker;
    }

    public static Marker addMarker(Context context, AMap aMap, int resId, LatLng latlng) {
        MarkerOptions options = new MarkerOptions();
        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(),
                resId)));
        options.anchor(0.5f, 0.5f);
        if (latlng != null) {
            options.position(latlng);
        }
        return aMap.addMarker(options);
    }

//    /**
//     * 设置终点Marker
//     */
//    public static Marker setWaitMarker(Context context, LatLng latLng, AMap aMap, int money, int time) {
//        View view = LayoutInflater.from(context).inflate(R.layout.window_info, null);
//        ((TextView) view.findViewById(R.id.money)).setText(money + "");
//        ((TextView) view.findViewById(R.id.tv_status)).setText(time + "");
//        final Marker marker = aMap.addMarker(new MarkerOptions().
//                position(latLng).
//                title("终点").
//                icon(BitmapDescriptorFactory.fromView(view)));
//        return marker;
//    }

    public static void initAnim(View v) {
        RotateAnimation animation = new RotateAnimation(0, 360, v.getPivotX(), v.getPivotY());
        animation.setInterpolator(new LinearInterpolator()); // 匀速旋转
        animation.setRepeatCount(Animation.INFINITE); // 无限重复
        animation.setDuration(500);
        v.setAnimation(animation);
        animation.setRepeatMode(Animation.RESTART);
        animation.start();
    }

    // 把LatLonPoint对象转化为LatLon对象
    public static LatLng convertToLatLng(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }

    public static LatLonPoint convertToPoint(LatLng latLng) {
        return new LatLonPoint(latLng.latitude, latLng.longitude);
    }

    public static NaviLatLng convertToNavi(LatLng latLng) {
        return new NaviLatLng(latLng.latitude, latLng.longitude);
    }

    public static NaviLatLng convertToNavi(LatLonPoint latLng) {
        return new NaviLatLng(latLng.getLatitude(), latLng.getLongitude());
    }

    public static LatLng convertNaviToLatLng(NaviLatLng latLng) {
        return new LatLng(latLng.getLatitude(), latLng.getLongitude());
    }

    public static LatLonPoint convertNaviToPoint(NaviLatLng latLng) {
        return new LatLonPoint(latLng.getLatitude(), latLng.getLongitude());
    }

    /**
     * 进行逆地理编码
     */
    public static void doReGeoSearch(Context context, final double lat, final double lng,
                                     final OnGeoSearchListener onGeoSearchListener) {
        GeocodeSearch geocoderSearch = new GeocodeSearch(context);
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                try {
                    if (regeocodeResult == null || regeocodeResult.getRegeocodeAddress() == null) {
                        return;
                    }
                    onGeoSearchListener.onReGeoSearched(regeocodeResult, i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });

        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(lat, lng), 2000,
                GeocodeSearch.AMAP);

        geocoderSearch.getFromLocationAsyn(query);
    }

    interface OnGeoSearchListener {
        void onReGeoSearched(RegeocodeResult regeocodeResult, int i);
    }
}
