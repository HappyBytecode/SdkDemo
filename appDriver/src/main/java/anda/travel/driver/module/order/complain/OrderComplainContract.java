package anda.travel.driver.module.order.complain;

import java.util.List;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.data.entity.ComplainEntity;

/**
 * 功能描述：
 */
public interface OrderComplainContract {

    interface View extends IBaseView<Presenter> {

        void showComplainMsg(List<ComplainEntity> entities);

        void complainSuccess(String content, String remark); //投诉成功

        void complained(String content, String remark, String complainResult, boolean isCommit, boolean isDealing); //此前的投诉
    }

    interface Presenter extends IBasePresenter {

        void setOrderUuid(String orderUuid);

        String getOrderUuid();

        void reqComplainStatus(); //获取投诉状态

        void reqComplainMsg();

        void submitComplain(String content, String remark);
    }
}
