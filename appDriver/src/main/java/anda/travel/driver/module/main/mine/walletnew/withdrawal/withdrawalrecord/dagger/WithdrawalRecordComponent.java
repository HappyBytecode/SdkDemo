package anda.travel.driver.module.main.mine.walletnew.withdrawal.withdrawalrecord.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.main.mine.walletnew.withdrawal.withdrawalrecord.WithdrawalRecordActivity;
import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = WithdrawalRecordModule.class)
public interface WithdrawalRecordComponent {
    void inject(WithdrawalRecordActivity activity);
}