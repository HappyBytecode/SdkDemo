package anda.travel.driver.module.main.mine.help.feedback.dagger;

import anda.travel.driver.module.main.mine.help.feedback.FeedbackContract;
import dagger.Module;
import dagger.Provides;

@Module
public class FeedbackModule {

    private final FeedbackContract.View mView;

    public FeedbackModule(FeedbackContract.View view) {
        mView = view;
    }

    @Provides
    FeedbackContract.View provideFeedbackContractView() {
        return mView;
    }
}
