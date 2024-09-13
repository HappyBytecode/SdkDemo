package anda.travel.driver.module.order.detail;

import android.content.Context;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.data.entity.OrderCostEntity;
import anda.travel.driver.data.entity.WarningContentEntity;
import anda.travel.driver.module.vo.OrderVO;

/**
 * 功能描述：
 */
public interface OrderDetailContract {

    interface View extends IBaseView<Presenter> {

        //设置订单信息
        void setOrderInfo(OrderVO vo);

        //显示订单金额
        void showTotalFare(double totalFare);

        //弹窗确认"跳转代付"
        void showPayConfirm();

        //跳转到订单支付页
        void skipToPayPumping(String orderUuid, double fare);

        void showWarningInfo(WarningContentEntity entity);

        void setDisplay(OrderCostEntity entity);

        Context getMyContext();
    }

    interface Presenter extends IBasePresenter {

        //设置当前订单编号
        void setOrderUuid(String orderUuid);

        //获取当前订单编号
        String getOrderUuid();

        //获取业务线编号
        String getBusinessUuid();

        //设置传递过来的OrderVO
        void setIntentOrderVO(OrderVO vo);

        //获取OrderVO
        OrderVO getOrderVO();

        //将mFirstSubscribe设置为false，以便首次获取订单详情时，优先从服务端获取
        void setOrderRefresh();

        //获取订单详情
        void reqOrderDetail();

        //保存金额
        void rushFare();

        //结束订单
        void completeOrder(int isPumping);

        //处理"订单状态错误"的情况
        void dealwithStatusError(Throwable ex, Integer isPumping);

        void warnCallback(String type, String warnUuid);

        void reqFareItems();
    }
}
