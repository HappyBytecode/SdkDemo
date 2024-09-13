package anda.travel.driver.module.car.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.car.SelectCarActivity;
import dagger.Component;

/**
 * 功能描述：
 */
@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = SelectCarModule.class)
public interface SelectCarComponent {

    void inject(SelectCarActivity activity);

}
