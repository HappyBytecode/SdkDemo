package anda.travel.driver.data.entity;

import com.amap.api.maps.model.LatLng;

/**
 * 数据点
 */
public class PointEntity {

    private String uuid; //订单编号
    private boolean isValid; //当前点是否有效
    private Long duration; //与上个有效点的时间间隔 (单位：毫秒)
    private Float distance; //距离上个有效点的里程 (单位：米)
    private Float avgSpeed; //上个有效点～当前点的平均速度 (单位：米/秒)
    private Float totalDistance; //当前总里程 (单位：米)

    private double lon;
    private double lat;
    private long loctime; //触发时间
    private float speed; //瞬时速度
    private float bearing; //角度

    private float accuracy; // 精度
    private boolean matchStatus; // -1－未知，0－未匹配到路径上，1－匹配到路径上

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Float getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(Float avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public Float getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Float totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public long getLoctime() {
        return loctime;
    }

    public void setLoctime(long loctime) {
        this.loctime = loctime;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public boolean getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(boolean matchStatus) {
        this.matchStatus = matchStatus;
    }

    /**
     * 获取经纬度
     *
     * @return
     */
    public LatLng getLatLng() {
        if (lat == 0 || lon == 0) return null;
        return new LatLng(lat, lon);
    }

}
