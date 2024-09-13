package anda.travel.driver.module.amap.heatmap.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.amap.heatmap.HeatMapActivity;
import dagger.Component;

/**
 * 功能描述：
 */
@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = HeatMapModule.class)
public interface HeatMapComponent {

    void inject(HeatMapActivity activity);

}
