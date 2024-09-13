package anda.travel.driver.module.main.mine.help.problem.dagger;

import anda.travel.driver.module.main.mine.help.problem.ProblemContract;
import dagger.Module;
import dagger.Provides;

@Module
public class ProblemModule {

    private final ProblemContract.View mView;

    public ProblemModule(ProblemContract.View view) {
        mView = view;
    }

    @Provides
    ProblemContract.View provideProblemContractView() {
        return mView;
    }
}
