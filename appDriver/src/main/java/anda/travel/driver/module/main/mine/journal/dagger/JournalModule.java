package anda.travel.driver.module.main.mine.journal.dagger;

import anda.travel.driver.module.main.mine.journal.JournalContract;
import dagger.Module;
import dagger.Provides;

@Module
public class JournalModule {
    private final JournalContract.View mView;

    public JournalModule(JournalContract.View view) {
        mView = view;
    }

    @Provides
    JournalContract.View providerJournalContractView() {
        return mView;
    }
}
