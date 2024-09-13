package anda.travel.driver.module.order.list.dagger;

import anda.travel.driver.module.order.list.OrderListContract;
import dagger.Module;
import dagger.Provides;

/**
 * 功能描述：
 */
@Module
public class OrderListModule {

    private final OrderListContract.View mView;

    public OrderListModule(OrderListContract.View view) {
        mView = view;
    }

    @Provides
    OrderListContract.View provideOrderListContractView() {
        return mView;
    }

}
