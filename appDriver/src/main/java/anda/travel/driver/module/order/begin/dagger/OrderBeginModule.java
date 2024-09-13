package anda.travel.driver.module.order.begin.dagger;

import anda.travel.driver.module.order.begin.OrderBeginContract;
import dagger.Module;
import dagger.Provides;

@Module
public class OrderBeginModule {

    private final OrderBeginContract.View mView;

    public OrderBeginModule(OrderBeginContract.View view) {
        mView = view;
    }

    @Provides
    public OrderBeginContract.View provideView() {
        return mView;
    }
}
