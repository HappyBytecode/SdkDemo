package anda.travel.driver.module.main.mine.journal.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.main.mine.journal.JournalActivity;
import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = JournalModule.class)
public interface JournalComponent {
    void inject(JournalActivity activity);
}
