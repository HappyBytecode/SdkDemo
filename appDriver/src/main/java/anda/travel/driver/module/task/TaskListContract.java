package anda.travel.driver.module.task;

import java.util.List;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.module.vo.TaskListVO;

public interface TaskListContract {

    interface View extends IBaseView<Presenter> {

        void showTaskList(List<TaskListVO> list);

    }

    interface Presenter extends IBasePresenter {

        void reqTaskList();

    }

}
