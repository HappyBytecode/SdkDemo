package anda.travel.driver.module.order.ongoing.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.order.ongoing.OrderOngoingFragment;
import dagger.Component;

/**
 * 功能描述：
 */
@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = OrderOngoingModule.class)
public interface OrderOngoingComponent {

    void inject(OrderOngoingFragment fragment);

}
