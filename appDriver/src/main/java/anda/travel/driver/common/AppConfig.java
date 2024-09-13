package anda.travel.driver.common;

import android.content.res.Resources;

import anda.travel.driver.R;
import anda.travel.driver.util.Env;
import timber.log.Timber;

/**
 * 功能描述：应用配置
 */
public class AppConfig {

    public static boolean showFloatWindow = false;

    public static int showFloatTime = 15;

    public final static String REQUEST_KEY = "010b05b0d83e4adeb3bbd0e01ca049c4c06dd63096ac44dc88b7a6988a3ae0ca";
    public final static String ORDER_STATUS_ERROR = "订单状态错误";

    public static String ANDA_APPKEY = ""; //安达AppKey
    public static String HOST = ""; //服务器地址
    public static String WS = ""; //长链接地址

    public static void initConfig(Resources res, Env env) {
        Timber.i("Env:%s", env);
        HOST = env.getApiHost();
        WS = env.getSocketHost();
        ANDA_APPKEY = res.getString(R.string.anda_appkey);
    }

    /**
     * 是否显示
     *
     * @return
     */
    public static boolean isShowFloatView() {
        return showFloatWindow;
    }

    public static void setFloatView(boolean isShow) {
        showFloatWindow = isShow;
    }

    public static int getShowFloatTime() {
        return showFloatTime;
    }

    public static void setShowFloatTime(int showFloatTime) {
        AppConfig.showFloatTime = showFloatTime;
    }

}
