package anda.travel.driver.data.entity;

/**
 * 接口返回热力图数据
 */
public class HeatMapEntity {

    /**
     * hour : 1  "1": 昨天，"2": 近3天，"3": 近7天
     * destLongitude : 31.902559
     * latitude : 31.838173
     * destLatitude : 117.274261
     * longitude : 117.368939
     */

    private int hour;
    private String destLongitude;
    private String latitude;
    private String destLatitude;
    private String longitude;

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public String getDestLongitude() {
        return destLongitude;
    }

    public void setDestLongitude(String destLongitude) {
        this.destLongitude = destLongitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getDestLatitude() {
        return destLatitude;
    }

    public void setDestLatitude(String destLatitude) {
        this.destLatitude = destLatitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
