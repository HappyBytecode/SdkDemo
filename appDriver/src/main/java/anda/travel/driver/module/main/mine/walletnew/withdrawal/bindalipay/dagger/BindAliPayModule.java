package anda.travel.driver.module.main.mine.walletnew.withdrawal.bindalipay.dagger;

import anda.travel.driver.module.main.mine.walletnew.withdrawal.bindalipay.BindAliPayContract;
import dagger.Module;
import dagger.Provides;

@Module
public class BindAliPayModule {
    private final BindAliPayContract.View mView;

    public BindAliPayModule(BindAliPayContract.View view) {
        mView = view;
    }

    @Provides
    BindAliPayContract.View provideBindAliPayContractView() {
        return mView;
    }
}
