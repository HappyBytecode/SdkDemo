package anda.travel.driver.module.main.mine.carinfo.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.main.mine.carinfo.CarInfoActivity;
import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = CarInfoModule.class)
public interface CarInfoComponent {
    void inject(CarInfoActivity activity);
}
