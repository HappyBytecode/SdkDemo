package anda.travel.driver.module.main.home.dagger;

import anda.travel.driver.module.main.home.HomeContract;
import dagger.Module;
import dagger.Provides;

/**
 * 功能描述：
 */
@Module
public class HomeModule {

    private final HomeContract.View mView;

    public HomeModule(HomeContract.View view) {
        mView = view;
    }

    @Provides
    HomeContract.View provideHomeContractView() {
        return mView;
    }

}
