package anda.travel.driver.module.main.duty.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.main.duty.DutyFragment;
import dagger.Component;

/**
 * 功能描述：
 */
@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = DutyModule.class)
public interface DutyComponent {

    void inject(DutyFragment dutyFragment);

}
