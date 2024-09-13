package anda.travel.driver.module.main.mine.walletnew.withdrawal.withdrawalrecord.withdrawldetails.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.main.mine.walletnew.withdrawal.withdrawalrecord.withdrawldetails.WithdrawDetailsActivity;
import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = WithdrawDetailsModule.class)
public interface WithdrawDetailsComponent {
    void inject(WithdrawDetailsActivity activity);
}
