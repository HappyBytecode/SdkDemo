package anda.travel.driver.module.main.mine.carinfo.mile.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.main.mine.carinfo.mile.MileActivity;
import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = MileModule.class)
public interface MileComponent {
    void inject(MileActivity activity);
}
