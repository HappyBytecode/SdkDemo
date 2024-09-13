package anda.travel.driver.module.main.mine.walletnew.withdrawal;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.data.entity.AliAccountEntity;

public interface WithdrawalContract {
    interface View extends IBaseView<Presenter> {
        /**
         * 显示绑定的支付宝账号信息
         */
        void showBingAliAccount(String bindAliAccount);

        /**
         * 显示司机最近提现成功账户查询信息
         */
        void showAliAccountBySuccess(AliAccountEntity aliAccountEntity);

        void sendCodeSuccess();

        /**
         * 提现成功
         */
        void withdrawalSucc(String zfbAccount);

        /**
         * 提现失败
         *
         * @param errCode 错误码
         * @param errMsg  错误原因
         */
        void withdrawalFail(int errCode, String errMsg);
    }

    interface Presenter extends IBasePresenter {

        void sendCode();

        /**
         * 查询司机绑定的支付宝账户
         */
        void findBindAliAccount();

        /**
         * 司机提现-司机最近提现成功账户查询
         */
        void findAliAccountBySuccess();

        /**
         * 绑定自己支付宝提现
         *
         * @param driverAccountUuid 司机编号
         * @param identifyCode      验证码
         * @param collectType       收款类型
         */
        void withdrawalOneSelf(String driverAccountUuid, String identifyCode, String collectAccount, int collectType);

        /**
         * 提现到他人支付宝提现
         *
         * @param driverAccountUuid 司机编号
         * @param identifyCode      验证码
         * @param collectName       支付宝账号姓名
         * @param collectAccount    账号
         * @param collectType       收款类型
         */
        void withdrawalOthers(String driverAccountUuid, String identifyCode, String collectName, String collectAccount, int collectType);
    }
}
