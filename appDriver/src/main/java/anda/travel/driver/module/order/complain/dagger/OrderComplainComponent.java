package anda.travel.driver.module.order.complain.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.order.complain.OrderComplainActivity;
import dagger.Component;

/**
 * 功能描述：
 */
@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = OrderComplainModule.class)
public interface OrderComplainComponent {

    void inject(OrderComplainActivity activity);

}
