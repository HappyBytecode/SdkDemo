package anda.travel.driver.module.task.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.task.TaskListActivity;
import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = TaskListModule.class)
public interface TaskListComponent {

    void inject(TaskListActivity activity);

}
