package anda.travel.driver.module.main.mine.help.dragger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.main.mine.help.HelpCenterActivity;
import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = HelpCenterModule.class)
public interface HelpCenterComponent {
    void inject(HelpCenterActivity activity);
}
