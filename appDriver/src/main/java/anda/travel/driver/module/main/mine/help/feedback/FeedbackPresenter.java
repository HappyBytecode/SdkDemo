package anda.travel.driver.module.main.mine.help.feedback;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.network.RetrofitUtil;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.module.vo.FeedbackImgVo;

public class FeedbackPresenter extends BasePresenter implements FeedbackContract.Presenter {
    private final UserRepository mUserRepository;
    private final FeedbackContract.View mView;

    @Inject
    public FeedbackPresenter(UserRepository userRepository, FeedbackContract.View view) {
        mUserRepository = userRepository;
        mView = view;
    }

    @Override
    public void addFeedBack(String advice, ArrayList<FeedbackImgVo> mList) {
        ArrayList<File> files = new ArrayList<>();
        for (FeedbackImgVo feedbackImgVo : mList) {
            if (!feedbackImgVo.isShowAddImg()) {
                File file = new File(feedbackImgVo.getPath());
                files.add(file);
            }
        }
        File[] arrayFiles = files.toArray(new File[files.size()]);
        mDisposable.add(mUserRepository.
                addFeedBack(RetrofitUtil.getRequestBody(advice),
                        RetrofitUtil.getRequestParts("files", arrayFiles))
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingViewWithDelay(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(
                        data -> mView.feedBackSuccess()
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)
                ));
    }

}
