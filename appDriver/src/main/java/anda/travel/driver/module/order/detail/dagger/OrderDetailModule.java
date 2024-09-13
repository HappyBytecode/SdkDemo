package anda.travel.driver.module.order.detail.dagger;

import anda.travel.driver.module.order.detail.OrderDetailContract;
import dagger.Module;
import dagger.Provides;

/**
 * 功能描述：
 */
@Module
public class OrderDetailModule {

    private final OrderDetailContract.View mView;

    public OrderDetailModule(OrderDetailContract.View view) {
        mView = view;
    }

    @Provides
    OrderDetailContract.View provideOrderDetailContractView() {
        return mView;
    }

}
