package anda.travel.driver.module.order.pay.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.order.pay.PayDialogFragment;
import dagger.Component;

/**
 * 功能描述：
 */
@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = OrderPayModule.class)
public interface OrderPayComponent {

    void inject(PayDialogFragment payDialogFragment);

}
