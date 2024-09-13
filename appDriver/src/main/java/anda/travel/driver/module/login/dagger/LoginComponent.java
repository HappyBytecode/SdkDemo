package anda.travel.driver.module.login.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.login.LoginFragment;
import dagger.Component;

/**
 * 功能描述：
 */
@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = LoginModule.class)
public interface LoginComponent {

    void inject(LoginFragment loginFragment);

}
