package anda.travel.driver.module.notice;

import java.util.List;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.data.entity.NoticeEntity;

public interface NoticeContract {

    interface View extends IBaseView<Presenter> {
        void showNoticeList(List<NoticeEntity> list);
    }

    interface Presenter extends IBasePresenter {
        void reqNoticeList();
    }
}
