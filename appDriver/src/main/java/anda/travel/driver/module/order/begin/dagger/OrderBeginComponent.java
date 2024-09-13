package anda.travel.driver.module.order.begin.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.order.begin.OrderBeginFragment;
import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = OrderBeginModule.class)
public interface OrderBeginComponent {

    void inject(OrderBeginFragment fragment);

}
