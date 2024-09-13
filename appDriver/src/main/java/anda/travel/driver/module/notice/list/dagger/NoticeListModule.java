package anda.travel.driver.module.notice.list.dagger;

import anda.travel.driver.module.notice.list.NoticeListContract;
import dagger.Module;
import dagger.Provides;

/**
 * 功能描述：
 */
@Module
public class NoticeListModule {

    private final NoticeListContract.View mView;

    public NoticeListModule(NoticeListContract.View view) {
        mView = view;
    }

    @Provides
    NoticeListContract.View provideView() {
        return mView;
    }

}
