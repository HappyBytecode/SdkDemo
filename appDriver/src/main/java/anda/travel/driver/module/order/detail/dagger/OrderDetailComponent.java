package anda.travel.driver.module.order.detail.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.order.details.OrderDetailFragment;
import dagger.Component;

/**
 * 功能描述：
 */
@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = OrderDetailModule.class)
public interface OrderDetailComponent {

    void inject(OrderDetailFragment orderDetailFragment);

}
