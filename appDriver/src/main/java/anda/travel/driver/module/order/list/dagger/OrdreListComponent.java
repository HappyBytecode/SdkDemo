package anda.travel.driver.module.order.list.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.order.list.OrderListFragment;
import dagger.Component;

/**
 * 功能描述：
 */
@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = OrderListModule.class)
public interface OrdreListComponent {

    void inject(OrderListFragment orderListFragment);

}
