package anda.travel.driver.module.main.mine.walletnew.dagger;

import anda.travel.driver.module.main.mine.walletnew.MyWalletNewContract;
import dagger.Module;
import dagger.Provides;

@Module
public class MyWalletNewModule {

    private final MyWalletNewContract.View mView;

    public MyWalletNewModule(MyWalletNewContract.View view) {
        mView = view;
    }

    @Provides
    MyWalletNewContract.View provideMyWalletNewContractView() {
        return mView;
    }
}
