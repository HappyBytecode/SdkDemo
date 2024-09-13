package anda.travel.driver.module.main.mine.setting.dagger;

import anda.travel.driver.module.main.mine.setting.SettingContract;
import dagger.Module;
import dagger.Provides;

@Module
public class SettingModule {

    private final SettingContract.View mView;

    public SettingModule(SettingContract.View view) {
        mView = view;
    }

    @Provides
    SettingContract.View provideSettingContractView() {
        return mView;
    }
}
