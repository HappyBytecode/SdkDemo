package anda.travel.driver.module.main.mine.help;

import android.content.Context;

import java.util.List;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.data.entity.ProblemEntity;

public interface HelpCenterContract {
    interface View extends IBaseView<Presenter> {
        /**
         * 显示常见问题列表
         *
         * @param faqs
         */
        void showFAQs(List<ProblemEntity> faqs);

        Context getContext();
    }

    interface Presenter extends IBasePresenter {
        /**
         * 获得常见问题列表
         */
        void reqFAQs();

        /**
         * 上报异常
         */
        void uploadError();
    }
}
