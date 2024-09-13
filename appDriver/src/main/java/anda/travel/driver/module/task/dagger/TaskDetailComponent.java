package anda.travel.driver.module.task.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.task.TaskDetailActivity;
import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = TaskDetailModule.class)
public interface TaskDetailComponent {

    void inject(TaskDetailActivity activity);

}
