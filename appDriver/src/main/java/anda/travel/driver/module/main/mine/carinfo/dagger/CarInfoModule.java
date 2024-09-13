package anda.travel.driver.module.main.mine.carinfo.dagger;

import anda.travel.driver.module.main.mine.carinfo.CarInfoContract;
import dagger.Module;
import dagger.Provides;

@Module
public class CarInfoModule {
    private final CarInfoContract.View mView;

    public CarInfoModule(CarInfoContract.View view) {
        mView = view;
    }

    @Provides
    CarInfoContract.View providerCarInfoContractView() {
        return mView;
    }
}
