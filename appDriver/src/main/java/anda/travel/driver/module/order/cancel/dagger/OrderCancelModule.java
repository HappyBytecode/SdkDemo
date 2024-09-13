package anda.travel.driver.module.order.cancel.dagger;

import anda.travel.driver.module.order.cancel.OrderCancelContract;
import dagger.Module;
import dagger.Provides;

/**
 * 功能描述：
 */
@Module
public class OrderCancelModule {

    private final OrderCancelContract.View mView;

    public OrderCancelModule(OrderCancelContract.View view) {
        mView = view;
    }

    @Provides
    OrderCancelContract.View provideView() {
        return mView;
    }

}
