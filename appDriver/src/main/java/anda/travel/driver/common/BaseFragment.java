package anda.travel.driver.common;

import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.baselibrary.base.LibBaseFragment;
import anda.travel.driver.common.dagger.AppComponent;

/**
 * 功能描述：基础Fragment
 */
public abstract class BaseFragment extends LibBaseFragment {

    public AppComponent getAppComponent() {
        return HxClientManager.getAppComponent();
    }

    public boolean isActive() {
        return isAdded();
    }

}