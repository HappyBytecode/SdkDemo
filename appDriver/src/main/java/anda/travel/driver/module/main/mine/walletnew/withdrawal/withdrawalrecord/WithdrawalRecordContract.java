package anda.travel.driver.module.main.mine.walletnew.withdrawal.withdrawalrecord;

import java.util.List;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.module.vo.WithdrawaleRecordVO;

public interface WithdrawalRecordContract {
    interface View extends IBaseView<Presenter> {
        /**
         * 显示提现记录
         *
         * @param vos
         */
        void showWithdrawalRecord(List<WithdrawaleRecordVO> vos);
    }

    interface Presenter extends IBasePresenter {
        /**
         * 获取提现记录
         */
        void reqWithdrawalRecord(int nowPage);
    }
}
