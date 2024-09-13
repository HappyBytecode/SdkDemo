package anda.travel.driver.module.order.list;

import java.util.HashMap;
import java.util.List;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.module.vo.OrderSummaryVO;
import anda.travel.driver.module.vo.OrderVO;

/**
 * 功能描述：
 */
public interface OrderListContract {

    interface View extends IBaseView<Presenter> {

        void setData(List<OrderSummaryVO> vos);

        void addData(List<OrderSummaryVO> vos);

        void onRefreshComplete();

        void noMore();

        void hideNoMore();

        void openOrderByStatus(OrderVO v); //根据订单状态，跳转相应的页面

        void openOrderFailed(OrderVO v); //根据订单状态，跳转相应的页面

        void netErrorView(boolean isShow);
    }

    interface Presenter extends IBasePresenter {

        void reqOrderList(HashMap<String, Integer> params);

        void reqOrderDetail(String orderUuid);

        void setCurOrderType(int curType);

        void rushFare(String orderUuid);

        void reload();
    }
}
