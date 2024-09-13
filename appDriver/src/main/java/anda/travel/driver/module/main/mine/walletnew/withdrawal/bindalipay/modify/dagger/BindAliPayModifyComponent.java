package anda.travel.driver.module.main.mine.walletnew.withdrawal.bindalipay.modify.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.main.mine.walletnew.withdrawal.bindalipay.modify.BindAliPayModifyActivity;
import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = BindAliPayModifyModule.class)
public interface BindAliPayModifyComponent {
    void inject(BindAliPayModifyActivity activity);
}