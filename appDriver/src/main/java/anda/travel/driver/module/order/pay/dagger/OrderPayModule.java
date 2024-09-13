package anda.travel.driver.module.order.pay.dagger;

import anda.travel.driver.module.order.pay.OrderPayContract;
import dagger.Module;
import dagger.Provides;

/**
 * 功能描述：
 */
@Module
public class OrderPayModule {

    private final OrderPayContract.View mView;

    public OrderPayModule(OrderPayContract.View view) {
        mView = view;
    }

    @Provides
    OrderPayContract.View provideView() {
        return mView;
    }

}
