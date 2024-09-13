package anda.travel.driver.data.entity;

import java.io.Serializable;
import java.util.List;

public class CarInfoEntity implements Serializable {
    public String todayMile;
    public String remainCharge;
    public String updateTime;
    public List<CarItemEntity> items;
    public String vin;
}
