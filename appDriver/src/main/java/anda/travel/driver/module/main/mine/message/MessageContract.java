package anda.travel.driver.module.main.mine.message;

import java.util.List;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.data.entity.HxMessageEntity;
import anda.travel.driver.module.vo.OrderVO;

/**
 * 功能描述：
 */
public interface MessageContract {

    interface View extends IBaseView<Presenter> {

        void setData(List<HxMessageEntity> vos);

        void addData(List<HxMessageEntity> vos);

        void onRefreshComplete();

        void noMore();

        /**
         * 删除消息列表成功
         */
        void cleanSucc();

        /**
         * 清空消息列表失败
         */
        void cleanFail(int errorCode, String errorMsg);

        void dealwithMessageAction(HxMessageEntity entity);

        void openOrderByStatus(OrderVO vo);
    }

    interface Presenter extends IBasePresenter {
        /**
         * 请求消息列表
         *
         * @param nowPage
         */
        void reqMessages(int nowPage);

        void readMessage(String msgUuid);

        /**
         * 清空消息列表
         */
        void cleanMessages();

        /**
         * 获取订单详情
         *
         * @param orderUuid
         */
        void reqOrderDetail(String orderUuid);
    }

}
