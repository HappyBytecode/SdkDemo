package anda.travel.driver.module.order.price;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.data.entity.OrderCostEntity;
import anda.travel.driver.module.vo.OrderVO;

/**
 * 功能描述：
 */
public interface PriceCheckContract {

    interface View extends IBaseView<Presenter> {

        void setDisplay(OrderCostEntity entity);

        void confirmFareSuccess(OrderVO vo);

        void resetDisplay();

        void judgeOrderStatus(OrderVO vo);

        void showCrossCityView();

        void showAbnormalView();

    }

    interface Presenter extends IBasePresenter {

        void setOrderUuid(String orderUuid);

        String getOrderUuid();

        void reqFareItems(); //获取费用明细

        void confirmFare(
                double highwayFare, //高速费
                double bridgeFare, //过桥费
                double parkingFare //停车费
        ); //确认费用

        void reqOrderDetail(); //刷新订单状态

    }

}
