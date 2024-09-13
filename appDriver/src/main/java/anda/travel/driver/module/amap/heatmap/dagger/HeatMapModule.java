package anda.travel.driver.module.amap.heatmap.dagger;

import anda.travel.driver.module.amap.heatmap.HeatMapContract;
import dagger.Module;
import dagger.Provides;

/**
 * 功能描述：
 */
@Module
public class HeatMapModule {

    private final HeatMapContract.View mView;

    public HeatMapModule(HeatMapContract.View view) {
        mView = view;
    }

    @Provides
    HeatMapContract.View provideView() {
        return mView;
    }

}
