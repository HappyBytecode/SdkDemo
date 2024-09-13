package anda.travel.driver.module.main.mine.walletnew.withdrawal.dagger;

import anda.travel.driver.module.main.mine.walletnew.withdrawal.WithdrawalContract;
import dagger.Module;
import dagger.Provides;

@Module
public class WithdrawalModule {
    private final WithdrawalContract.View mView;

    public WithdrawalModule(WithdrawalContract.View view) {
        mView = view;
    }

    @Provides
    WithdrawalContract.View provideWithdrawalContractView() {
        return mView;
    }
}
