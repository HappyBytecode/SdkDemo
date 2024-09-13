package anda.travel.driver.module.main.mine.setting.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.main.mine.setting.SettingActivity;
import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = SettingModule.class)
public interface SettingComponent {
    void inject(SettingActivity activity);
}
