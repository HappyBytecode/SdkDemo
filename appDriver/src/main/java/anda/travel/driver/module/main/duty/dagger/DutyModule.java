package anda.travel.driver.module.main.duty.dagger;

import anda.travel.driver.module.main.duty.DutyContract;
import dagger.Module;
import dagger.Provides;

/**
 * 功能描述：
 */
@Module
public class DutyModule {

    private final DutyContract.View mView;

    public DutyModule(DutyContract.View view) {
        mView = view;
    }

    @Provides
    DutyContract.View provideDutyContractView() {
        return mView;
    }

}
