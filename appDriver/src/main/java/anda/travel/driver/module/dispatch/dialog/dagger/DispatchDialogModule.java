package anda.travel.driver.module.dispatch.dialog.dagger;

import anda.travel.driver.module.dispatch.dialog.DisPatchDialogContract;
import dagger.Module;
import dagger.Provides;

@Module
public class DispatchDialogModule {

    private final DisPatchDialogContract.View mView;

    public DispatchDialogModule(DisPatchDialogContract.View view) {
        mView = view;
    }

    @Provides
    DisPatchDialogContract.View provideDispatchDialogContractView() {
        return mView;
    }

}
