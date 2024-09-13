package anda.travel.driver.module.car.dagger;

import anda.travel.driver.module.car.SelectCarContract;
import dagger.Module;
import dagger.Provides;

/**
 * 功能描述：
 */
@Module
public class SelectCarModule {

    private final SelectCarContract.View mView;

    public SelectCarModule(SelectCarContract.View view) {
        mView = view;
    }

    @Provides
    SelectCarContract.View provideView() {
        return mView;
    }

}
