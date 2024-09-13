package anda.travel.driver.module.main.mine.walletnew.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.main.mine.walletnew.MyWalletNewActivity;
import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = MyWalletNewModule.class)
public interface MyWalletNewComponent {
    void inject(MyWalletNewActivity activity);
}
