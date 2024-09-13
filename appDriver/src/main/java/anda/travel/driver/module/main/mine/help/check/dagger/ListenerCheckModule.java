package anda.travel.driver.module.main.mine.help.check.dagger;

import anda.travel.driver.module.main.mine.help.check.ListenerCheckContract;
import dagger.Module;
import dagger.Provides;

@Module
public class ListenerCheckModule {

    private final ListenerCheckContract.View mView;

    public ListenerCheckModule(ListenerCheckContract.View view) {
        mView = view;
    }

    @Provides
    ListenerCheckContract.View provideView() {
        return mView;
    }

}
