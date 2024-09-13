package anda.travel.driver.module.main.mine.walletnew.withdrawal.withdrawalrecord.withdrawldetails.dagger;

import anda.travel.driver.module.main.mine.walletnew.withdrawal.withdrawalrecord.withdrawldetails.WithdrawDetailsContract;
import dagger.Module;
import dagger.Provides;

@Module
public class WithdrawDetailsModule {

    private final WithdrawDetailsContract.View mView;

    public WithdrawDetailsModule(WithdrawDetailsContract.View view) {
        mView = view;
    }

    @Provides
    WithdrawDetailsContract.View provideWithdrawDetailsContractView() {
        return mView;
    }
}
