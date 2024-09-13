package anda.travel.driver.module.main.mine.walletnew.withdrawal.bindalipay;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;

public interface BindAliPayContract {
    interface View extends IBaseView<Presenter> {

        void sendCodeSuccess();

        /**
         * 绑定/修改支付宝账户成功
         */
        void showBindAliAccountSuccess(String zfbAccount);
    }

    interface Presenter extends IBasePresenter {

        void sendCode();

        /**
         * 绑定支付宝账号
         */
        void bindAliAccount(String mobile, String identifyCode);
    }
}
