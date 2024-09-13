package anda.travel.driver.api;

import anda.travel.driver.BuildConfig;

/**
 * 功能描述：
 */
public final class ApiConfig {

    private static String HOST = ""; //服务器地址
    private static String APP_URL_DOMAIN = HOST + "";
    public static String H5_DOWNLOAD_URL;

    //   ------ API 分类 ------   //
    private static String DRIVER_API = ""; //用户相关
    private static String ORDER_API = ""; //出租车、专车订单
    private static String CONFIG_URL = ""; //配置文件下载地址
    private static String ROOT_URL = "";

    private ApiConfig() {

    }

    public static void setHost(String host) {
        HOST = host;
        APP_URL_DOMAIN = HOST;
        DRIVER_API = APP_URL_DOMAIN + "/api/driver/";
        ORDER_API = APP_URL_DOMAIN + "/api/driver/token/";
        CONFIG_URL = APP_URL_DOMAIN + "/api/driver/config/android";
        ROOT_URL = APP_URL_DOMAIN;
        if (BuildConfig.DEBUG) {
            H5_DOWNLOAD_URL = "http://192.168.100.232/download";
        } else {
            H5_DOWNLOAD_URL = "https://oss.hexingyueche.com/download";
        }
    }

    public static String getDriverApi() {
        return DRIVER_API;
    }

    public static String getOrderApi() {
        return ORDER_API;
    }

    public static String getConfigUrl() {
        return CONFIG_URL;
    }

    public static String getRootApi() {
        return ROOT_URL;
    }

}
