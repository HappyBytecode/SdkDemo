package anda.travel.driver.data.entity;

import anda.travel.driver.baselibrary.utils.NumberUtil;
import anda.travel.driver.baselibrary.utils.TypeUtil;

public class CheckStatusEntity {

    public String cityName; //城市名
    public String vehicleLevel; //车型
    public String pushDistance; //推单距离

    public String getCityName() {
        return TypeUtil.getValue(cityName);
    }

    public String getVehicleLevel() {
        return TypeUtil.getValue(vehicleLevel);
    }

    public String getPushDistance() {
        return TypeUtil.getValue(pushDistance);
    }

    public String getPushDistanceWithFormat() {
        try {
            float distance = Float.parseFloat(pushDistance) / 1000;
            return NumberUtil.getFormatValue(distance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "3";
    }

}
