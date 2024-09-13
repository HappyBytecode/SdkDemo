package anda.travel.driver.module.main.mine.walletnew.withdrawal.bindalipay.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.main.mine.walletnew.withdrawal.bindalipay.BindAliPayActivity;
import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = BindAliPayModule.class)
public interface BindAliPayComponent {
    void inject(BindAliPayActivity activity);
}