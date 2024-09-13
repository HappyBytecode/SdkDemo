package anda.travel.driver.module.dispatch.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.dispatch.DispatchFragment;
import dagger.Component;

/**
 * 功能描述：
 */
@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = DispatchModule.class)
public interface DispatchComponent {

    void inject(DispatchFragment fragment);

}
