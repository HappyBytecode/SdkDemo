package anda.travel.driver.module.main.mine.walletnew.withdrawal.withdrawalrecord.withdrawldetails;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.module.vo.WithdrawDetailVO;

public interface WithdrawDetailsContract {
    interface View extends IBaseView<Presenter> {
        void showWithdrawalDetails(WithdrawDetailVO vo);
    }

    interface Presenter extends IBasePresenter {
        void reqWithdrawalDetails(String cashUuid);

        void reqOwnWithdrawalDetails(String cashUuid);
    }
}
