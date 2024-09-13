package anda.travel.driver.module.amap;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.NaviInfo;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;

/**
 * 功能描述：
 */
public interface ANavigateContract {

    interface View extends IBaseView<Presenter> {

        void showTrafficLine(boolean showTrafficLine); //显示交通状况

        void setMapPadding(int top, int bottom); //设置地图上边距和下边距

        void locate(LatLng center); //定位到当前位置

        void calculateRoute(LatLng origin, LatLng dest);

        void navigate(LatLng origin, LatLng dest, int routeId); //导航到目的地

        void setOrderPoint(LatLng origin, LatLng dest); //设置订单起终点

        void postNaviInfo(NaviInfo info); //发送导航信息

        void emulator(boolean isEmulator); //是否开启模拟导航

        void showLateTime(long lateTime);

        void hideLateTime();
    }

    interface Presenter extends IBasePresenter {
        LatLng getCurrentLatLng(); //获取当前司机位置
    }
}
