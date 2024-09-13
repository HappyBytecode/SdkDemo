package anda.travel.driver.module.main.mine.message.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.main.mine.message.MessageFragment;
import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = MessageModule.class)
public interface MessageComponent {
    void inject(MessageFragment activity);
}