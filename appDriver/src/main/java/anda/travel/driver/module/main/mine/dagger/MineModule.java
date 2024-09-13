package anda.travel.driver.module.main.mine.dagger;

import anda.travel.driver.module.main.mine.MineContract;
import dagger.Module;
import dagger.Provides;

/**
 * 功能描述：
 */
@Module
public class MineModule {

    private final MineContract.View mView;

    public MineModule(MineContract.View view) {
        mView = view;
    }

    @Provides
    MineContract.View provideMineContractView() {
        return mView;
    }

}
