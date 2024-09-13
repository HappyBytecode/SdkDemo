package anda.travel.driver.module.order.popup;

import android.content.Context;

import com.amap.api.maps.model.LatLng;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.module.vo.OrderVO;

/**
 * 功能描述：
 */
public interface OrderPopupContract {

    interface View extends IBaseView<Presenter> {

        //设置订单信息
        void setOrderInfo(OrderVO vo, LatLng curLatLng);

        //抢单成功
        void grabSuccess(OrderVO vo, boolean isDistribute);

        void grabFail(int time, String reason);

        Context getContext();

        void showGrabBtn(int time);

        //关闭界面
        void closeActivity();

        //关闭界面 true-手动关闭(按"返回键"或点"关闭键") false-弹窗有效时间内未处理，自动关闭
        void closeActivityByRefuse(boolean isPress);

        void showDispatchBtn(int time);

        void dispatchConfirm();

        void initDispatch(OrderVO vo);
    }

    interface Presenter extends IBasePresenter {

        //设置当前订单编号
        void setOrderUuid(String orderUuid);

        String getOrderUuid();

        void getOrderByGrab();

        //抢单失败
        void grabFail(String failReason);

        LatLng getLatLng();

        //是否为"派单"或"改派"订单
        boolean getIsDistributeOrRedistribute();

        ////判断是否为派单
        boolean getIsDistribute();
    }
}
