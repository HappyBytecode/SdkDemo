package anda.travel.driver.module.main.mine.walletnew.balancedetail.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.main.mine.walletnew.balancedetail.BalanceDetailActivity;
import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = BalanceDetailModule.class)
public interface BalanceDetailComponent {
    void inject(BalanceDetailActivity activity);
}
