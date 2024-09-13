package anda.travel.driver.module.main.mine.walletnew.withdrawal.bindalipay.modify.dagger;

import anda.travel.driver.module.main.mine.walletnew.withdrawal.bindalipay.modify.BindAliPayModifyContract;
import dagger.Module;
import dagger.Provides;

@Module
public class BindAliPayModifyModule {
    private final BindAliPayModifyContract.View mView;

    public BindAliPayModifyModule(BindAliPayModifyContract.View view) {
        mView = view;
    }

    @Provides
    BindAliPayModifyContract.View provideBindAliPayModifyContractView() {
        return mView;
    }
}
