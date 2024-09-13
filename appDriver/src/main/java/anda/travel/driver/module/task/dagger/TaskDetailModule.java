package anda.travel.driver.module.task.dagger;

import anda.travel.driver.module.task.TaskDetailContract;
import dagger.Module;
import dagger.Provides;

@Module
public class TaskDetailModule {

    private final TaskDetailContract.View mView;

    public TaskDetailModule(TaskDetailContract.View view) {
        mView = view;
    }

    @Provides
    public TaskDetailContract.View provideView() {
        return mView;
    }

}
