package anda.travel.driver.module.main.duty;

import android.content.Context;

import java.util.HashMap;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.data.entity.DriverEntity;
import anda.travel.driver.data.entity.OrderListenerEntity;
import anda.travel.driver.data.entity.WarningContentEntity;
import anda.travel.driver.module.vo.OrderVO;

/**
 * 功能描述："出车/收车"状态层Contract
 */
public interface DutyContract {

    interface View extends IBaseView<Presenter> {

        Context getContext();

        /**
         * 显示出车
         */
        void showOnDuty();

        /**
         * 显示收车
         */
        void showOffduty();

        /**
         * 打开新订单弹窗
         *
         * @param orderUuid
         */
        void openOrderPopup(String orderUuid, OrderVO vo);

        void showWarningInfo(WarningContentEntity entity);

        void dismissWarningInfo();

        /**
         * 当前是否正在后台运行
         *
         * @return
         */
        boolean isBackground();

        void showNetworkDisconnect(boolean isDisconnect);

        void showForceDutyOnNotice(String notice);

        /**
         * 被强制下线
         *
         * @param notice
         */
        void showForceOffDuty(String notice);

        void openOrderSetting();

        /**
         * 获取人脸活体检测token成功
         *
         * @param ticketId
         * @param token
         */
        void getTokenSuccess(boolean isForce, String ticketId, String token);

        /**
         * 出车按钮是否可点
         */
        void setOnDutyEnable(boolean isEnable);
    }

    interface Presenter extends IBasePresenter {

        /**
         * 获取是否出车的状态
         */
        void reqDutyStatus(boolean onlyFromRemote);

        /**
         * 正常出车
         */
        void reqOnDuty(String bizId);

        /**
         * 强制出车
         */
        void forceReqOnDuty(String bizId);

        /**
         * 被强制收车
         */
        void forceOffDuty(String notice);

        /**
         * 收车
         */
        void reqOffDuty(boolean showVoice, boolean isForce);

        /**
         * 获取订单详情
         */
        void reqOrderDetail(String orderUuid, boolean isDistribute, boolean isRedistribute, Integer loops, Integer loopCnt);

        void warnCallback(String type, String warnUuid);

        void getWarning();

        /**
         * 调试系统推送
         */
        void testSystemPush();

        /**
         * 拒绝派单
         *
         * @param params
         */
        void refuseOrder(HashMap<String, String> params);

        /**
         * 获取听单设置
         *
         * @return
         */
        OrderListenerEntity getListenerSetting();

        DriverEntity getDriverEntityFromLocal();

        void openOrderSetting();

        /**
         * 获取人脸活体检测token
         */
        void getVerifyToken(boolean isForce);
    }
}
