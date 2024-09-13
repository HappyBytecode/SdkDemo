package anda.travel.driver.module.main.mine.journal;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.data.entity.JournalEntity;

public interface JournalContract {
    interface View extends IBaseView<JournalContract.Presenter> {
        /**
         * 设置流水明细数据
         *
         * @param entity
         */
        void setData(JournalEntity entity);

        /**
         * 添加流水明细数据
         *
         * @param entity
         */
        void addData(JournalEntity entity);

        /**
         * 刷新完成
         */
        void onRefreshComplete();

        void noMore();

        void hideNoMore();
    }

    interface Presenter extends IBasePresenter {
        /**
         * 获取流水数据
         */
        void reqJournal(String startTime, String endTime, int page);
    }
}
