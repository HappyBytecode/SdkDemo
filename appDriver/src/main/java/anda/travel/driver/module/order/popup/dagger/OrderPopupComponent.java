package anda.travel.driver.module.order.popup.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.order.popup.OrderPopupFragment;
import dagger.Component;

/**
 * 功能描述：
 */
@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = OrderPopupModule.class)
public interface OrderPopupComponent {

    void inject(OrderPopupFragment orderPopupFragment);

}
