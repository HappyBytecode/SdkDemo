package anda.travel.driver.module.main.mine.message.dagger;

import anda.travel.driver.module.main.mine.message.MessageContract;
import dagger.Module;
import dagger.Provides;

@Module
public class MessageModule {

    private final MessageContract.View mView;

    public MessageModule(MessageContract.View view) {
        mView = view;
    }

    @Provides
    MessageContract.View provideMessageContractView() {
        return mView;
    }
}
