package anda.travel.driver.module.order.price.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.order.price.PriceCheckActivity;
import dagger.Component;

/**
 * 功能描述：
 */
@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = PriceCheckModule.class)
public interface PriceCheckComponent {

    void inject(PriceCheckActivity activity);

}
