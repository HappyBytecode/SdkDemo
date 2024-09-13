package anda.travel.driver.module.main.home;

import android.content.Context;

import java.util.List;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.data.ad.AdvertisementEntity;
import anda.travel.driver.data.entity.DynamicIconEntity;
import anda.travel.driver.data.entity.HomeEntity;
import anda.travel.driver.data.entity.HtmlActEntity;
import anda.travel.driver.data.entity.HxMessageEntity;
import anda.travel.driver.module.vo.DispatchVO;
import anda.travel.driver.module.vo.OrderVO;

/**
 * 功能描述："订单、收入、消息"展示层Contract
 */
public interface HomeContract {

    interface View extends IBaseView<Presenter> {

        /**
         * 显示信息
         *
         * @param vo
         */
        void showHomePageInfo(HomeEntity vo);

        /**
         * 关闭下拉刷新
         */
        void hideRefreshing();

        void openOrderByStatus(OrderVO vo);

        void showNetworkNotice(boolean disconnect);

        /**
         * 显示未读消息
         *
         * @param list
         */
        void showUnreadMessage(List<HxMessageEntity> list);

        /**
         * 将系统消息从列表中移除
         *
         * @param entity
         */
        void removeMessage(HxMessageEntity entity);

        /**
         * 清除所有系统消息的显示
         */
        void clearAllMessage();

        /**
         * 跳转到对应的网页
         *
         * @param url
         * @param title
         */
        void openWebUrl(String url, String title);

        /**
         * 提示"有进行中的订单"
         *
         * @param orderUuid
         */
        void showOrderOngoing(String orderUuid);

        /**
         * 提示"有预约单超过出发时间"
         *
         * @param orderUuid
         */
        void showAppointBegin(OrderVO orderUuid);

        /**
         * 提示"有预约单即将开始"
         *
         * @param orderUuid
         */
        void showAppointNotice(OrderVO orderUuid);

        /**
         * 处理系统消息的跳转
         *
         * @param entity
         */
        void dealWithMessage(HxMessageEntity entity);

        Context getContext();

        void showDispatchCompleteDialog(String reason);

        void showDispatchRemindDialog(DispatchVO dispatchVO);

        void openOrderFailed();

        /* 展示活动模块列表 */
        void showModuleList(List<HtmlActEntity> vos);

        void skipToAd(List<AdvertisementEntity> entities);

        void skipToAdTransverse(List<AdvertisementEntity> entities);

        void hideOrderInfo();

        void setMainImages(DynamicIconEntity data);

        void showDefaultModel();

        void hideDefaultModel();

        /**
         * 未读消息（服务通知+对话）数量
         *
         * @param noReadCount
         */
        void postUnreadCount(int noReadCount);
    }

    interface Presenter extends IBasePresenter {
        /**
         * 获取"订单/收入等"信息
         */
        void reqWorkInfo();

        /**
         * 获取首页订单状态
         */
        void reqHomeStatus();

        /**
         * 出发去接乘客
         *
         * @param orderUuid
         */
        void reqOrderBegin(String orderUuid);

        /**
         * 获取订单详情
         *
         * @param orderUuid
         */
        void reqOrderDetail(String orderUuid);

        /**
         * @param orderUuid
         * @param msgEntity
         */
        void reqOrderDetail(String orderUuid, HxMessageEntity msgEntity);

        /**
         * 获取未读服务通知数量
         */
        void reqAllUnreadMessagesCount(int page);

        /**
         * 获取未读系统消息
         */
        void reqAllUnreadMessages();

        /**
         * 将消息设置为已读
         *
         * @param entity
         */
        void readMessage(HxMessageEntity entity);

        /**
         * 更新司机当前的登录信息
         */
        void recording();

        /**
         * 获取调度信息
         *
         * @return
         */
        DispatchVO getDispatchVO();

        void downloadHtml(Context context);

        /**
         * 获取首页弹窗广告,开屏广告，首页banner
         *
         * @param isUseCache 是否使用缓存
         */
        void getAD(boolean isUseCache);

        /**
         * 获取订单详情（新）
         *
         * @param orderUuid
         */
        void reqOrderDetailNew(String orderUuid, int status);

        void getMainImages();

    }

}
