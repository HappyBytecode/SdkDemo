package anda.travel.driver.module.notice.list;

import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.data.entity.NoticeEntity;
import anda.travel.driver.data.user.UserRepository;

/**
 * 功能描述：
 */
public class NoticeListPresenter extends BasePresenter implements NoticeListContract.Presenter {

    private final NoticeListContract.View mView;
    private final UserRepository mUserRepository;

    @Inject
    public NoticeListPresenter(NoticeListContract.View view, UserRepository userRepository) {
        mView = view;
        mUserRepository = userRepository;
    }

    @Override
    public void reqNoticeList() {
        List<NoticeEntity> list = mUserRepository.getLocalNoticeList();
        mView.showNoticeList(list);
    }
}
