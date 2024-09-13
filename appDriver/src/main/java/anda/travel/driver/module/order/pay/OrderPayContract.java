package anda.travel.driver.module.order.pay;

import android.content.Context;

import java.util.List;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.data.entity.PayTypeEntity;
import anda.travel.driver.data.entity.WxPayInfo;

/**
 * 功能描述：
 */
public interface OrderPayContract {

    interface View extends IBaseView<Presenter> {

        void showPriceInfo(double price); //显示订单金额

        void paySuccess(); //支付成功

        void payFail(); //支付失败

        void startAlipay(String tradeUrl); //执行支付宝支付

        void startWxpay(WxPayInfo info); //执行微信支付

        void showPayTypeList(List<PayTypeEntity> list);

        Context getContext();

    }

    interface Presenter extends IBasePresenter {

        boolean getIsDependDriver();

        void setOrderUuid(String orderUuid);

        String getOrderUuid();

        //微信支付
        void reqPayByWeixin(String ip);

        //支付宝支付
        void reqPayByAlipay();

        //余额支付
        void reqPayByBalance();

        //获取支付方式列表
        void reqPayTypeList();

    }

}
