package anda.travel.driver.module.order.begin;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.module.vo.OrderVO;

public interface OrderBeginContract {

    interface View extends IBaseView<Presenter> {

        //设置显示
        void setDisplay(OrderVO vo);

        //弹窗确认"出发去接乘客"
        void showBeginConfirm();

        //"出发去接乘客"接口调用成功
        void orderBeginSuccess(String orderUuid, OrderVO vo);

        //重置按键显示
        void resetBtnDisplay();

        //显示订单已被改派
        void showOrderDistributToOther(String notice); //订单被改派给

        void jchatDisplay();

        void closeView();
    }

    interface Presenter extends IBasePresenter {

        //获取乘客联系电话
        String getPassengerPhone();

        //获取订单详情
        OrderVO getOrderVO();

        //获取订单详情
        void reqOrderDetail();

        //出发去接乘客
        void reqOrderBegin();

        //处理订单状态异常的情况
        void dealwithStatusError(Throwable ex);
    }

}
