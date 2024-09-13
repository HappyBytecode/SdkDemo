package anda.travel.driver.module.main.mine.walletnew.balancedetail.dagger;

import anda.travel.driver.module.main.mine.walletnew.balancedetail.BalanceDetailContract;
import dagger.Module;
import dagger.Provides;

@Module
public class BalanceDetailModule {

    private final BalanceDetailContract.View mView;

    public BalanceDetailModule(BalanceDetailContract.View view) {
        mView = view;
    }

    @Provides
    BalanceDetailContract.View provideBalanceDetailContractView() {
        return mView;
    }
}
