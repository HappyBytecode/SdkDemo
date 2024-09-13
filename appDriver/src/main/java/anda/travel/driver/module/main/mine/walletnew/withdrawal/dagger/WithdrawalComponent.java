package anda.travel.driver.module.main.mine.walletnew.withdrawal.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.main.mine.walletnew.withdrawal.WithdrawalActivity;
import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = WithdrawalModule.class)
public interface WithdrawalComponent {
    void inject(WithdrawalActivity activity);
}