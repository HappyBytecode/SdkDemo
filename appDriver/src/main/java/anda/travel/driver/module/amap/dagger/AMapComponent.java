package anda.travel.driver.module.amap.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.amap.AMapFragment;
import dagger.Component;

/**
 * 功能描述：
 */
@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = AMapModule.class)
public interface AMapComponent {

    void inject(AMapFragment aMapFragment);

}
