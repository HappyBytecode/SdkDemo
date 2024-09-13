package anda.travel.driver.module.order.ongoing;

import android.content.Context;
import android.os.Bundle;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.AMapNaviLocation;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.module.vo.OrderVO;

/**
 * 功能描述：
 */
public interface OrderOngoingContract {

    interface View extends IBaseView<Presenter> {

        void setDisplay();

        void resetBtnDisplay(); //重置按键显示

        void arriveStart();

        void passengerGetOn();

        void arriveEnd(OrderVO orderVO); //已到达上车地点

        void closeActivity(); //关闭界面

        void setNaviInfoDisplay(Integer distance, Integer time);

        void skipToNavigate(); //跳转到独立的导航页

        void setTotalPrice(double totalPrice); //设置订单金额

        void showEmulatorBtn(); //显示模拟导航按钮

        void changeEmulator(); //切换导航模式

        void showLateTime(String strLateTim, int type); //显示迟到时间,type 三种状态，

        //免费等待，等待超时计费。超时计费中
        void showOrderDistributToOther(String notice); //订单被改派给

        void speechAppInBackground(); //播报应用处于后台

        void jchatDisplay();

        void audioPermission();

        void showAbnormalView();

        void showCrossCityView();

        Context getContext();
    }

    interface Presenter extends IBasePresenter {

        void reqOrderDetail();

        void switchToNextStatus(); //切换到下个订单状态

        void reqArriveStart(); //到达上车地点

        void lateBilling(); //开始"迟到计费"

        void reqPassengerGetOn(); //乘客已上车

        void reqArriveEnd(); //到达目的地

        void dealWithStatusError(Throwable ex); //处理"订单状态错误"的情况

        LatLng getCurrentLatLng(); //获取司机当前坐标

        void dealWithAMapNaviLocation(AMapNaviLocation location);

        //是否显示模拟导航按键
        boolean isEmulatorOpen();

        //处理迟到时间
        void dealWithLateTime(Long lateTime, Long realLateTime);

        //记录"开始前往目的地"的时间戳
        void recordDepartTimeStamp();

        void onSaveInstanceState(Bundle outState);

        void onRestoreInstanceState(Bundle savedInstanceState);

        void startRecording();
    }
}
