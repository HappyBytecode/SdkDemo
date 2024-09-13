package anda.travel.driver.client.message.body;

import anda.travel.driver.client.message.Body;

/**
 * body-登录
 *
 * @author Zoro
 * @date 2017/3/27
 */
public class Login implements Body {

    public static final int FROM_PASSENGER = 1;
    public static final int FROM_DRIVER = 2;

    private int from;//来自乘客或者司机
    private String token;
    private String osName; //操作系统
    private String osVersion; //系统版本
    private String appVersion; //app版本
    private String deviceToken; //设备标识

    public Login() {

    }

    public Login(int from, String token) {
        this.from = from;
        this.token = token;
    }

    public Login(int from, String token, String osName, String osVersion, String appVersion, String deviceToken) {
        this.from = from;
        this.token = token;
        this.osName = osName;
        this.osVersion = osVersion;
        this.appVersion = appVersion;
        this.deviceToken = deviceToken;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

}
