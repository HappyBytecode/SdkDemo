package anda.travel.driver.module.order.popup.dagger;

import anda.travel.driver.module.order.popup.OrderPopupContract;
import dagger.Module;
import dagger.Provides;

/**
 * 功能描述：
 */
@Module
public class OrderPopupModule {

    private final OrderPopupContract.View mView;

    public OrderPopupModule(OrderPopupContract.View view) {
        mView = view;
    }

    @Provides
    OrderPopupContract.View provideOrderPopupContractView() {
        return mView;
    }

}
