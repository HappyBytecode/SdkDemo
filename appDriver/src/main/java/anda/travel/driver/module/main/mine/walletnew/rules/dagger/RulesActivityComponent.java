package anda.travel.driver.module.main.mine.walletnew.rules.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.main.mine.walletnew.rules.RulesActivity;
import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = RulesActivityModule.class)
public interface RulesActivityComponent {
    void inject(RulesActivity activity);
}
