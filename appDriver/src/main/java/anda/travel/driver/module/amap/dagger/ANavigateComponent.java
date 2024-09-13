package anda.travel.driver.module.amap.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.amap.ANavigateFragment;
import dagger.Component;

/**
 * 功能描述：
 */
@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = ANavigateModule.class)
public interface ANavigateComponent {

    void inject(ANavigateFragment fragment);

}
