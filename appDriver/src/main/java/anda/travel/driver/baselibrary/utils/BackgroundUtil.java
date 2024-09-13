package anda.travel.driver.baselibrary.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;

import java.util.List;

public class BackgroundUtil {

    public static final int RECEPTION = 1; ////在前台

    /**
     * 应用是否处于后台
     *
     * @param context
     * @return
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                return appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
            }
        }
        return true;
    }

    /**
     * 是否锁屏
     *
     * @param context
     * @return
     */
    public static boolean isScreenOn(Context context) {
        PowerManager manager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return manager.isInteractive();
        } else {
            return manager.isScreenOn();
        }
    }

    /**
     * 获取"应用所处状态"
     *
     * @param context
     * @return 1-前台,2-后台,3-锁屏
     */
    public static int getAppStatus(Context context) {
        if (!BackgroundUtil.isScreenOn(context)) {
            return 3;
        }
        if (BackgroundUtil.isBackground(context)) {
            return 2;
        }
        return 1;
    }

    public static String getAppStatus(int appStatus) {
        switch (appStatus) {
            case 1:
            case 2:
            case 3:
                return "前台";
            default:
                return "未知";
        }
    }

    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(30);

        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().contains(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}
