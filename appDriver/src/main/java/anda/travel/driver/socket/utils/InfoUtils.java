package anda.travel.driver.socket.utils;

import android.text.TextUtils;

import anda.travel.driver.data.entity.DriverEntity;
import timber.log.Timber;

/**
 * @Author moyuwan
 * @Date 17/12/6
 */
public class InfoUtils {

    private static InfoUtils mInstance;

    private InfoUtils() {
    }

    public static InfoUtils get() {
        if (mInstance == null) {
            synchronized (InfoUtils.class) {
                if (mInstance == null) mInstance = new InfoUtils();
            }
        }
        return mInstance;
    }

    private String token;
    private String clientUuid;
    private String vehicleUuid; //车辆uuid
    private String vehLvUuid; //车型uuid
    private int depend; //是否是自营司机
    private int vehDepend; //车辆的归属（自有or挂靠）

    //缓存用户信息
    public void setEntity(DriverEntity entity) {
        token = entity.token;
        clientUuid = entity.uuid;
        vehicleUuid = entity.vehicleUuid;
        vehLvUuid = entity.vehLvUuid;
        depend = entity.depend == null ? 0 : entity.depend;
        vehDepend = entity.vehDepend == null ? 0 : entity.vehDepend;
        Timber.i("-----> 获取用户信息：\ntoken = " + token +
                "\nclientUuid = " + clientUuid +
                "\nvehicleUuid = " + vehicleUuid +
                "\nvehLvUuid = " + vehLvUuid +
                "\ndepend = " + depend +
                "\nvehDepend = " + vehDepend);
    }

    //重置缓存信息
    public void reset() {
        token = clientUuid = vehicleUuid = vehLvUuid = null;
        depend = vehDepend = 0;
    }

    /**
     * 判断是否是同个账号的消息
     *
     * @param msgClientUuid
     * @return
     */
    public boolean clientIsCorrect(String msgClientUuid) {
        if (TextUtils.isEmpty(clientUuid)) return false;
        if (TextUtils.isEmpty(msgClientUuid)) return false;
        return msgClientUuid.equals(clientUuid);
    }

    /* ***** 获取信息 ***** */

    public String getToken() {
        return token;
    }

    public String getClientUuid() {
        return clientUuid;
    }

    public String getVehicleUuid() {
        return vehicleUuid;
    }

    public String getVehLvUuid() {
        return vehLvUuid;
    }

    public int getDepend() {
        return depend;
    }

    public int getVehDepend() {
        return vehDepend;
    }

}
