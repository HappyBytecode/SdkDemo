package anda.travel.driver.module.task;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.module.vo.TaskListVO;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TaskListPresenter extends BasePresenter implements TaskListContract.Presenter {

    private final TaskListContract.View mView;
    private final UserRepository mUserRepository;

    @Inject
    public TaskListPresenter(TaskListContract.View view, UserRepository userRepository) {
        mView = view;
        mUserRepository = userRepository;
    }

    @Override
    public void reqTaskList() {
        mDisposable.add(mUserRepository.getTaskList()
                .flatMapIterable(entities -> entities)
                .map(TaskListVO::createFrom)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> mView.showLoadingViewWithDelay(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(mView::showTaskList
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

}
