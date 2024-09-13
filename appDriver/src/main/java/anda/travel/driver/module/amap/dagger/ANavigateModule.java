package anda.travel.driver.module.amap.dagger;

import anda.travel.driver.module.amap.ANavigateContract;
import dagger.Module;
import dagger.Provides;

/**
 * 功能描述：
 */
@Module
public class ANavigateModule {

    private final ANavigateContract.View mView;

    public ANavigateModule(ANavigateContract.View view) {
        mView = view;
    }

    @Provides
    ANavigateContract.View provideView() {
        return mView;
    }

}
