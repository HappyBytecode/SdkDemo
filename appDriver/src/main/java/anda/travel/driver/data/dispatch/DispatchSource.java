package anda.travel.driver.data.dispatch;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.NaviLatLng;

import anda.travel.driver.data.entity.PointEntity;
import anda.travel.driver.module.vo.DispatchVO;

interface DispatchSource {

    void setIsDispatchDisplay(boolean isDispatchDisplay);

    boolean getIsDispatchDisplay();

    /**
     * 创建导航
     */
    void createNavi();

    /**
     * 销毁导航
     */
    void destoryNavi();

    /**
     * 设置调度信息，结束时传null
     *
     * @param dispatchVO
     */
    void setDispatch(DispatchVO dispatchVO);

    /**
     * 获取调度信息
     *
     * @return
     */
    DispatchVO getDispatch();

    /**
     * @param sLatlng
     * @param eLatlng
     */
    void startNavi(NaviLatLng sLatlng, NaviLatLng eLatlng);

    /**
     * 获取剩余里程
     *
     * @return
     */
    Integer getRetainDistance();

    /**
     * 获取剩余时间
     *
     * @return
     */
    Integer getRetainTime();

    /**
     * 获取当前位置（仅调度使用）
     *
     * @return
     */
    LatLng getDispatchCurrentLatLng();

    boolean getDispatchEmulator();

    void setDispatchEmulator(boolean dispatchEmulator);

    void addPoint(PointEntity point);

    double queryTotalDistance();

    void dispatchComplete(String orderUuid);

}
