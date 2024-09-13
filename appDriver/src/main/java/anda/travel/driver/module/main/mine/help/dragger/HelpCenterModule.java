package anda.travel.driver.module.main.mine.help.dragger;

import anda.travel.driver.module.main.mine.help.HelpCenterContract;
import dagger.Module;
import dagger.Provides;

@Module
public class HelpCenterModule {

    private final HelpCenterContract.View mView;

    public HelpCenterModule(HelpCenterContract.View view) {
        mView = view;
    }

    @Provides
    HelpCenterContract.View provideHelpCenterContractView() {
        return mView;
    }
}
