package anda.travel.driver.module.dispatch.dagger;

import anda.travel.driver.module.dispatch.DispatchContract;
import dagger.Module;
import dagger.Provides;

/**
 * 功能描述：
 */
@Module
public class DispatchModule {

    private final DispatchContract.View mView;

    public DispatchModule(DispatchContract.View view) {
        mView = view;
    }

    @Provides
    DispatchContract.View provideView() {
        return mView;
    }

}
