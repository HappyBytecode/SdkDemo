package anda.travel.driver.module.main.mine.help.check.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.main.mine.help.check.ListenerCheckActivity;
import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = ListenerCheckModule.class)
public interface ListenerCheckComponent {

    void inject(ListenerCheckActivity activity);

}
