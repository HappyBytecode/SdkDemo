package anda.travel.driver.common;

import android.app.Activity;

import java.util.Stack;

public class AppManager {

    private static Stack<Activity> activityStack;
    private static AppManager instance;

    private AppManager() {

    }

    public static AppManager getInstance() {
        if (instance == null) {
            synchronized (AppManager.class) {
                if (instance == null) instance = new AppManager();
            }
        }
        return instance;
    }

    /**
     * 将界面添加到Stack中
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) activityStack = new Stack<>();
        activityStack.add(activity);
    }

    /**
     * 将界面从Stack中移除
     *
     * @param activity
     */
    public void removeActivity(Activity activity) {
        if (activityStack != null) activityStack.remove(activity);
    }

    /**
     * 获取当前界面
     *
     * @return
     */
    public Activity currentActivity() {
        if (activityStack == null || activityStack.isEmpty()) return null;
        return activityStack.lastElement();
    }

    /**
     * 关闭当前界面
     */
    public void finishActivity() {
        Activity activity = currentActivity();
        finishActivity(activity);
    }

    /**
     * 关闭指定界面
     *
     * @param activity
     */
    private void finishActivity(Activity activity) {
        if (activity == null) return;

        removeActivity(activity);
        activity.finish();
    }

    /**
     * 关闭指定类型的界面
     *
     * @param cls
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 关闭所有界面
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 除LoginActivity以外的界面，是否都已关闭
     *
     * @return
     */
    public boolean isAllActivityClosed() {
        return activityStack == null || activityStack.isEmpty();
    }

}
