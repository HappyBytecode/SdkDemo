package anda.travel.driver.module.main.mine.walletnew.withdrawal.withdrawalrecord.dagger;

import anda.travel.driver.module.main.mine.walletnew.withdrawal.withdrawalrecord.WithdrawalRecordContract;
import dagger.Module;
import dagger.Provides;

@Module
public class WithdrawalRecordModule {
    private final WithdrawalRecordContract.View mView;

    public WithdrawalRecordModule(WithdrawalRecordContract.View view) {
        mView = view;
    }

    @Provides
    WithdrawalRecordContract.View provideWithdrawalRecordContractView() {
        return mView;
    }
}
