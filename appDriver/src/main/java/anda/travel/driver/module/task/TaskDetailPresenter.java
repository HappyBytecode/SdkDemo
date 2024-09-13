package anda.travel.driver.module.task;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.module.vo.TaskDetailVO;

public class TaskDetailPresenter extends BasePresenter implements TaskDetailContract.Presenter {

    private final TaskDetailContract.View mView;
    private final UserRepository mUserRepository;
    private String mTaskUuid;

    @Inject
    public TaskDetailPresenter(TaskDetailContract.View view, UserRepository userRepository) {
        mView = view;
        mUserRepository = userRepository;
    }

    @Override
    public void onCreate(String taskUuid) {
        mTaskUuid = taskUuid;
        reqTaskDetail();
    }

    @Override
    public void reqTaskDetail() {
        mDisposable.add(mUserRepository.getTaskDetail(mTaskUuid)
                .map(TaskDetailVO::createFrom)
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingView(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(mView::showTaskDetail
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

}
