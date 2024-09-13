package anda.travel.driver.module.amap.dagger;

import anda.travel.driver.module.amap.AMapContract;
import dagger.Module;
import dagger.Provides;

/**
 * 功能描述：
 */
@Module
public class AMapModule {

    private final AMapContract.View mView;

    public AMapModule(AMapContract.View view) {
        mView = view;
    }

    @Provides
    AMapContract.View provideView() {
        return mView;
    }

}
