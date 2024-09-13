package anda.travel.driver.module.dispatch.dialog.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.dispatch.dialog.DispatchDialogActivity;
import dagger.Component;

/**
 * 功能描述：
 */
@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = DispatchDialogModule.class)
public interface DispatchDialogComponent {

    void inject(DispatchDialogActivity dispatchDialogActivity);

}
