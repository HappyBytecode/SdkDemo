package anda.travel.driver.module.main.mine.walletnew.rules;

import java.util.List;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.data.entity.WithdrawRuleEntity;

public interface RulesContract {
    interface View extends IBaseView<Presenter> {
        /**
         * 显示规则
         *
         * @param list
         */
        void showRules(List<WithdrawRuleEntity> list);
    }

    interface Presenter extends IBasePresenter {
        /**
         * 获取加盟司机余额提现规则
         */
        void reqRules();

        /**
         * 获取加盟司机账单说明规则
         */
        void reqBillRules();
    }
}
