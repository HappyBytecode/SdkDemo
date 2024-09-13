package anda.travel.driver.module.order.complain.dagger;

import anda.travel.driver.module.order.complain.OrderComplainContract;
import dagger.Module;
import dagger.Provides;

/**
 * 功能描述：
 */
@Module
public class OrderComplainModule {

    private final OrderComplainContract.View mView;

    public OrderComplainModule(OrderComplainContract.View view) {
        mView = view;
    }

    @Provides
    OrderComplainContract.View provideView() {
        return mView;
    }

}
