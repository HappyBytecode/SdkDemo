package anda.travel.driver.module.login.dagger;

import anda.travel.driver.module.login.LoginContract;
import dagger.Module;
import dagger.Provides;

/**
 * 功能描述：
 */
@Module
public class LoginModule {

    private final LoginContract.View mView;

    public LoginModule(LoginContract.View view) {
        mView = view;
    }

    @Provides
    LoginContract.View provideLoginContractView() {
        return mView;
    }

}
