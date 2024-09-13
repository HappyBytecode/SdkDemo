package anda.travel.driver.baselibrary.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import timber.log.Timber;

public class VersionUtil {

    /**
     * 获取显示的版本号
     *
     * @param context
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "1.0";
        try {
            String packageName = context.getPackageName();
            verName = context.getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    /**
     * 得到软件版本号
     *
     * @param context 上下文
     * @return 当前版本Code
     */
    public static int getVerCode(Context context) {
        if (context == null) return 1;
        int verCode = -1;
        try {
            String packageName = context.getPackageName();
            verCode = context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e);
        }
        return verCode;
    }
}
