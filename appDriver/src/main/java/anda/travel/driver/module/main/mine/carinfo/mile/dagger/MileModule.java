package anda.travel.driver.module.main.mine.carinfo.mile.dagger;

import anda.travel.driver.module.main.mine.carinfo.mile.MileContract;
import dagger.Module;
import dagger.Provides;

@Module
public class MileModule {
    private final MileContract.View mView;

    public MileModule(MileContract.View view) {
        mView = view;
    }

    @Provides
    MileContract.View providerMileContractView() {
        return mView;
    }
}
