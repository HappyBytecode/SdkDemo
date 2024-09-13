package anda.travel.driver.util;

import android.content.Context;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;

import java.util.List;

import rx.Observable;

/**
 * Created by Ling on 2017.3.27
 * 路径规划  不跟地图关联
 */
public class RouteUtil implements RouteSearch.OnRouteSearchListener {

    private OnRouteResultListener mListener;

    public interface OnRouteResultListener {
        void onRouteResultSuccess(DrivePath drivePath);

        void onRouteResultFailed();
    }

    public void setOnRouteResultListener(OnRouteResultListener onRouteResultListener) {
        mListener = onRouteResultListener;
    }

    /**
     * @param mStartPoint 起点
     * @param mEndPoint   终点
     */
    public void driveRouter(Context context, LatLonPoint mStartPoint, LatLonPoint mEndPoint) {
        RouteSearch routeSearch = new RouteSearch(context);
        routeSearch.setRouteSearchListener(this);
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartPoint, mEndPoint);
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, null, null, "");
        routeSearch.calculateDriveRouteAsyn(query);
    }

    /**
     * @param mStartPoint    起点
     * @param mEndPoint      终点
     * @param passedByPoints 途径点
     */
    public void driveRouter(Context context, LatLonPoint mStartPoint, LatLonPoint mEndPoint, List<LatLonPoint> passedByPoints) {
        RouteSearch routeSearch = new RouteSearch(context);
        routeSearch.setRouteSearchListener(this);
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartPoint, mEndPoint);
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DRIVING_SINGLE_DEFAULT, passedByPoints, null, "");
        routeSearch.calculateDriveRouteAsyn(query);
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
        //公交车
    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        if (errorCode == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    final DrivePath drivePath = result.getPaths()
                            .get(0);
                    if (mListener != null) mListener.onRouteResultSuccess(drivePath);
                } else {
                    if (mListener != null) mListener.onRouteResultFailed();
                    //ToastUtil.getInstance().toast("无结果");
                }
            } else {
                //ToastUtil.getInstance().toast("无结果");
                if (mListener != null) mListener.onRouteResultFailed();
            }
        } else {
            if (mListener != null) mListener.onRouteResultFailed();
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
        //步行
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    public Observable<DriveRouteResult> routeSearch(Context context, LatLonPoint start, LatLonPoint end, List<LatLonPoint> passedByPoints) {
        return Observable.create(subscriber -> {
            RouteSearch routeSearch = new RouteSearch(context);
            RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(start, end);
            int strategy = SysConfigUtils.get().getNaviStrategy();
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, strategy,
                    passedByPoints, null, "");
            routeSearch.calculateDriveRouteAsyn(query);
            // 路线规划的回调
            routeSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
                @Override
                public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

                }

                @Override
                public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
                    if (rCode == 1000) {
                        if (result != null && result.getPaths() != null && result.getPaths().size() > 0) {
                            subscriber.onNext(result);
                        } else {
                            subscriber.onError(new Throwable("对不起，没有搜索到相关数据！"));
                        }
                    } else if (rCode == 27) {
                        subscriber.onError(new Throwable("搜索失败,请检查网络连接！"));
                    } else if (rCode == 32) {
                        subscriber.onError(new Throwable("Key 验证无效！"));
                    } else {
                        subscriber.onError(new Throwable("未知错误，请稍后重试！错误码为" + rCode));
                    }
                }

                @Override
                public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

                }

                @Override
                public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

                }
            });
        });
    }
}
