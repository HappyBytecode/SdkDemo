package anda.travel.driver.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class BaseTaskSwitch implements Application.ActivityLifecycleCallbacks {

    private int mCount = 0;
    private OnTaskSwitchListener mOnTaskSwitchListener;
    private static BaseTaskSwitch mBaseLifecycle;
    private int mActivityCount = 0;

    public static BaseTaskSwitch init(Application application) {
        if (null == mBaseLifecycle) {
            mBaseLifecycle = new BaseTaskSwitch();
            application.registerActivityLifecycleCallbacks(mBaseLifecycle);
        }
        return mBaseLifecycle;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mActivityCount++;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (mCount++ == 0) {
            mOnTaskSwitchListener.onTaskSwitchToForeground();
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (--mCount == 0) {
            mOnTaskSwitchListener.onTaskSwitchToBackground();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mActivityCount--;
        if (mActivityCount == 0) {
            mOnTaskSwitchListener.onApplicationDestory();
        }
    }

    public void setOnTaskSwitchListener(OnTaskSwitchListener listener) {
        mOnTaskSwitchListener = listener;
    }

    public interface OnTaskSwitchListener {

        void onTaskSwitchToForeground();

        void onTaskSwitchToBackground();

        void onApplicationDestory();
    }
}
