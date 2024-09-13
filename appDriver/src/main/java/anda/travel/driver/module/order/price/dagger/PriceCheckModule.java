package anda.travel.driver.module.order.price.dagger;

import anda.travel.driver.module.order.price.PriceCheckContract;
import dagger.Module;
import dagger.Provides;

/**
 * 功能描述：
 */
@Module
public class PriceCheckModule {

    private final PriceCheckContract.View mView;

    public PriceCheckModule(PriceCheckContract.View view) {
        mView = view;
    }

    @Provides
    PriceCheckContract.View provideView() {
        return mView;
    }

}
