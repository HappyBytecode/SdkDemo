package anda.travel.driver.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;

import anda.travel.driver.BuildConfig;
import anda.travel.driver.baselibrary.network.RequestParams;
import anda.travel.driver.baselibrary.utils.SP;
import timber.log.Timber;

/**
 * 获取设备相关信息
 */
@SuppressLint({"HardwareIds", "MissingPermission"})
public class DeviceUtil {
    private static final String IMEI_KEY = "IMEI_PRIFIX";
    private static final String IMSI_KEY = "IMSI_PRIFIX";
    private static final String ANDROID_ID_KEY = "ANDROID_ID_PRIFIX";

    /**
     * 获取设备和版本相关信息
     *
     * @param context context
     * @return builder
     */
    public static RequestParams.Builder getDeviceInfo(Context context) {
        String deviceID = getIMEI(context);

        RequestParams.Builder builder = new RequestParams.Builder();
        builder.putParam("deviceToken", deviceID)
                .putParam("mac", TextUtils.isEmpty(getMac()) ? "" : getMac())
                .putParam("imei", deviceID)
                .putParam("imsi", TextUtils.isEmpty(getIMSI(context)) ? "" : getIMSI(context))
                .putParam("ip", DeviceUtil.getIPAddress(context));
        return builder;
    }

    public static String getIMEI(Context context) {
        String imei = SP.getInstance(context).getString(IMEI_KEY, "");
        if (TextUtils.isEmpty(imei)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                String androidId = getAndroidId(context);
                if (!TextUtils.isEmpty(androidId)) {
                    imei = androidId;
                } else {
                    imei = getUUID();
                }
            } else {
                imei = "";
            }
            if (TextUtils.isEmpty(imei)) {
                imei = getUUID();
            }
            SP.getInstance(context).putString(IMEI_KEY, imei);
        }
        return imei;
    }

    /**
     * 获取手机IMSI号
     */
    public static String getIMSI(Context context) {
        String imsi = SP.getInstance(context).getString(IMSI_KEY, "");
        if (TextUtils.isEmpty(imsi)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                String androidId = getAndroidId(context);
                if (!TextUtils.isEmpty(androidId)) {
                    imsi = androidId;
                } else {
                    imsi = getUUID();
                }
            } else {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
                    imsi = "";
                } else {
                    TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    imsi = mTelephonyMgr.getSubscriberId();
                }
            }
            if (TextUtils.isEmpty(imsi)) {
                imsi = getUUID();
            }
            SP.getInstance(context).putString(IMSI_KEY, imsi);
        }
        return imsi;
    }

    public static String getMac() {
        String address = null;
        // 把当前机器上的访问网络接口的存入 Enumeration集合中
        Enumeration<NetworkInterface> interfaces;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface netWork = interfaces.nextElement();
                // 如果存在硬件地址并可以使用给定的当前权限访问，则返回该硬件地址（通常是 MAC）。
                byte[] by = netWork.getHardwareAddress();
                if (by == null || by.length == 0) {
                    continue;
                }
                StringBuilder builder = new StringBuilder();
                for (byte b : by) {
                    builder.append(String.format("%02X:", b));
                }
                if (builder.length() > 0) {
                    builder.deleteCharAt(builder.length() - 1);
                }
                String mac = builder.toString();
                Timber.d("interfaceName=" + netWork.getName() + ", mac=" + mac);
                // 从路由器上在线设备的MAC地址列表，可以印证设备Wifi的 name 是 wlan0
                if (netWork.getName().equals("wlan0")) {
                    Timber.d(" interfaceName =" + netWork.getName() + ", mac=" + mac);
                    address = mac;
                }
            }
            return address;
        } catch (SocketException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取IP
     *
     * @param context context
     * @return ip
     */
    public static String getIPAddress(Context context) {
        @SuppressLint("MissingPermission") NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                return intIP2StringIP(wifiInfo.getIpAddress());
            }
        } else {
            //当前无网络连接,请在设置中打开网络
            return "";
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip ip
     * @return ip
     */
    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    public static String getDeviceVersion() {
        return android.os.Build.MODEL + " -- " + android.os.Build.VERSION.RELEASE; //设备版本
    }

    /**
     * 获取操作系统
     *
     * @return model
     */
    public static String getOsName() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取系统版本
     *
     * @return osVersion
     */
    public static String getOsVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取app版本
     *
     * @return appVersion
     */
    public static String getAppVersion() {
        return BuildConfig.VERSION_NAME;
    }

    /**
     * 获取设备标识
     *
     * @return deviceId
     */
    public static String getDeviceToken(Context context) {  //设备id
        String imei = SP.getInstance(context).getString(IMEI_KEY, "");
        if (TextUtils.isEmpty(imei)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                imei = getUUID();
            } else {
                imei = "";
            }
            if (TextUtils.isEmpty(imei)) {
                imei = getUUID();
            }
            SP.getInstance(context).putString(IMEI_KEY, imei);
        }
        return imei;
    }

    public static String getUUID() {
        String imei;
        UUID uuid = UUID.randomUUID();
        if (uuid == null) {
            imei = System.currentTimeMillis() + "";
        } else {
            imei = uuid.toString();
        }
        return imei;
    }

    private static String getAndroidId(Context context) {
        String androidId = SP.getInstance(context).getString(ANDROID_ID_KEY, "");
        if (TextUtils.isEmpty(androidId)) {
            androidId = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            SP.getInstance(context).putString(ANDROID_ID_KEY, androidId);
        }
        return androidId;
    }

}
