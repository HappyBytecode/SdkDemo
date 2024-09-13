package anda.travel.driver.module.amap.assist;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;

public class CalculateUtils {
    public static float calculateLineDistance(LatLng first, LatLng second) {
        float distance;
        try {
            distance = AMapUtils.calculateLineDistance(first, second);
        } catch (Exception e) {
            //////////高德传入的经纬度有误
            e.printStackTrace();
            return 0f;
        }
        return distance;
    }
}
