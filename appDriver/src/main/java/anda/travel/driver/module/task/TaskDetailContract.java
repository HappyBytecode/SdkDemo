package anda.travel.driver.module.task;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.module.vo.TaskDetailVO;

public interface TaskDetailContract {

    interface View extends IBaseView<Presenter> {

        void showTaskDetail(TaskDetailVO vo);

    }

    interface Presenter extends IBasePresenter {

        void onCreate(String taskUuid);

        void reqTaskDetail();

    }

}
