package anda.travel.driver.module.vo;

import java.io.Serializable;

/**
 * 功能描述：司机信息
 */
public class DriverVO implements Serializable {

    public Double lng; //经度
    public Double lat; //纬度
    public Double mileage; //已行驶的里程
    public int interval;
}
