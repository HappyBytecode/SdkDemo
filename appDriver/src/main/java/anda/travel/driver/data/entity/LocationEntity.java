package anda.travel.driver.data.entity;

import java.io.Serializable;

import anda.travel.driver.config.IConstants;

/**
 * 功能描述：位置信息
 */
public class LocationEntity implements Serializable {

    public String adcode = IConstants.DefaultAdcode; //当前用户所在区域编码
    public double lng; //当前用所在经度
    public double lat; //当前用所在纬度
    public long timeStmap; //记录当前位置时的时间戳

    public float angle; //角度
    public float speed; //速度

    public String address; //地址

    public float accuracy; //精度

    @Override
    public String toString() {
        return "LocationEntity{" +
                "adcode='" + adcode + '\'' +
                ", lng=" + lng +
                ", lat=" + lat +
                ", timeStmap=" + timeStmap +
                ", angle=" + angle +
                ", speed=" + speed +
                ", address='" + address + '\'' +
                ", accuracy=" + accuracy +
                '}';
    }
}
