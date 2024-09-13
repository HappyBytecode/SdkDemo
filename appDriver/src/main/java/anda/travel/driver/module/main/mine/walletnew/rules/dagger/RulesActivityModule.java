package anda.travel.driver.module.main.mine.walletnew.rules.dagger;

import anda.travel.driver.module.main.mine.walletnew.rules.RulesContract;
import dagger.Module;
import dagger.Provides;

/**
 * Created by FTL
 */
@Module
public class RulesActivityModule {

    private final RulesContract.View mView;

    public RulesActivityModule(RulesContract.View view) {
        mView = view;
    }

    @Provides
    RulesContract.View provideRulesActivityContractView() {
        return mView;
    }
}
