package anda.travel.driver.module.main.mine.help.feedback;

import java.util.ArrayList;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.module.vo.FeedbackImgVo;

public interface FeedbackContract {
    interface View extends IBaseView<Presenter> {
        void feedBackSuccess();
    }

    interface Presenter extends IBasePresenter {
        void addFeedBack(String advice, ArrayList<FeedbackImgVo> mList);
    }
}
