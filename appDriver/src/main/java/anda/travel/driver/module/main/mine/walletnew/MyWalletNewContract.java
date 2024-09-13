package anda.travel.driver.module.main.mine.walletnew;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.data.entity.CashSettingEntity;
import anda.travel.driver.data.entity.MyWalletEntity;
import anda.travel.driver.data.entity.RentBillInfoEntity;
import anda.travel.driver.data.entity.WithdrawRuleEntity;

public interface MyWalletNewContract {

    interface View extends IBaseView<Presenter> {

        void showCashSetting(CashSettingEntity entity);

        void showMyWallet(MyWalletEntity myWalletEntity);

        void showRentBillInfo(RentBillInfoEntity rentBillInfoEntity);

        void showNotAvailableMoneyDesc(WithdrawRuleEntity ruleEntity);

        /**
         * 刷新完成
         */
        void refreshComplete();
    }

    interface Presenter extends IBasePresenter {

        /**
         * 获取提现配置（司机钱包数据）
         */
        void reqMyWallet();

        /**
         * 获取自营提现配置
         */
        void reqOwnCashSetting();

        /**
         * 获取司机租金账单信息
         */
        void reqRentBillInfo();

        /**
         * 获取司机钱包-未入账金额说明
         */
        void reqGetNotAvailableMoneyDesc();

        /**
         * 保存账号是否第一次进入新版钱包
         */
        void saveIsFirstMyWallet(boolean isFirstMyWallet);

        /**
         * 获得登录账户是否第一次进入新版钱包
         */
        boolean getIsFirstMyWallet();

    }
}
