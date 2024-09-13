package anda.travel.driver.module.task.dagger;

import anda.travel.driver.module.task.TaskListContract;
import dagger.Module;
import dagger.Provides;

@Module
public class TaskListModule {

    private final TaskListContract.View mView;

    public TaskListModule(TaskListContract.View view) {
        mView = view;
    }

    @Provides
    public TaskListContract.View provideView() {
        return mView;
    }

}
