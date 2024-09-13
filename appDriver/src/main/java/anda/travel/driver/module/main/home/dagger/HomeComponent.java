package anda.travel.driver.module.main.home.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.main.home.HomeFragment;
import dagger.Component;

/**
 * 功能描述：
 */
@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = HomeModule.class)
public interface HomeComponent {

    void inject(HomeFragment homeFragment);

}
