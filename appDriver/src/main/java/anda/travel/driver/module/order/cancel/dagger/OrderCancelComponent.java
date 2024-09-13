package anda.travel.driver.module.order.cancel.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.order.cancel.OrderCancelActivity;
import dagger.Component;

/**
 * 功能描述：
 */
@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = OrderCancelModule.class)
public interface OrderCancelComponent {

    void inject(OrderCancelActivity activity);

}
