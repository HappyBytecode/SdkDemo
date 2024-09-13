package anda.travel.driver.module.order.ongoing.dagger;

import anda.travel.driver.module.order.ongoing.OrderOngoingContract;
import dagger.Module;
import dagger.Provides;

/**
 * 功能描述：
 */
@Module
public class OrderOngoingModule {

    private final OrderOngoingContract.View mView;

    public OrderOngoingModule(OrderOngoingContract.View view) {
        mView = view;
    }

    @Provides
    OrderOngoingContract.View provideView() {
        return mView;
    }

}
