package anda.travel.driver.module.order.cancel;

import java.util.List;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.data.entity.CancelReasonEntity;

/**
 * 功能描述：
 */
public interface OrderCancelContract {

    interface View extends IBaseView<Presenter> {

        void showCancelMsg(List<CancelReasonEntity> entities);

        void cancelSuccess(String orderUuid); //取消成功

    }

    interface Presenter extends IBasePresenter {

        void setOrderUuid(String orderUuid, int status);

        String getOrderUuid();

        void reqCancelMsg();

        void submitCancel(String content);

    }

}
