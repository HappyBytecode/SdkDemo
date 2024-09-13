package anda.travel.driver.module.notice.dagger;

import anda.travel.driver.module.notice.NoticeContract;
import dagger.Module;
import dagger.Provides;

/**
 * 功能描述：
 */
@Module
public class NoticeModule {

    private final NoticeContract.View mView;

    public NoticeModule(NoticeContract.View view) {
        mView = view;
    }

    @Provides
    NoticeContract.View provideView() {
        return mView;
    }

}
