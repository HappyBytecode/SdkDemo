package anda.travel.driver.module.notice;

import javax.inject.Inject;

import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.data.user.UserRepository;

/**
 * 功能描述：
 */
public class NoticePresenter extends BasePresenter implements NoticeContract.Presenter {

    private final NoticeContract.View mView;
    private final UserRepository mUserRepository;

    @Inject
    public NoticePresenter(NoticeContract.View view, UserRepository userRepository) {
        mView = view;
        mUserRepository = userRepository;
    }

    @Override
    public void reqNoticeList() {
        mDisposable.add(mUserRepository.getNoticeList()
                .compose(RxUtil.applySchedulers())
                .flatMapIterable(entities -> entities)
                .toList()
                .subscribe(mView::showNoticeList, ex -> {
                    //暂不处理
                    //showNetworkError(ex, R.string.network_error, mView, mUserRepository);
                }));
    }
}
