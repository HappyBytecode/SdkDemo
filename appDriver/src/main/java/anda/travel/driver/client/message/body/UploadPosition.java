package anda.travel.driver.client.message.body;

import anda.travel.driver.client.message.Body;

/**
 * UploadPosition
 *
 * @author Zoro
 * @date 2016/12/28
 */
public class UploadPosition implements Body {

    public Boolean isPresent; //是否为当前点（需修改）

    private String adcode; //行政区域编码
    private String locationUuid;//位置点uuid           ture
    private String driverUuid;//司机uuid               ture
    private String vehicleUuid;//车辆uuid              ture
    private String vehLvUuid;//车型uuid                ture
    private String orderUuid;//订单uuid                false
    private String passengerUuid;//乘客uuid            false
    private Double distance;//与上一点的距离           ture
    private String lat;//纬度                           ture
    private String lng;//经度                           ture
    private Double angle;//角度                         ture
    private String appid;//appid                        ture
    // 新加字段
    private Double mileage;// 订单中累计距离            false
    private Long uploadTime;// 上传时间（时间戳）       ture
    private Integer orderStatus;// 订单状态                false
    private Double speed;//当前速度                     ture
    private Integer isListen;//是否听单（出车）【1:听单（出车）；2：不听单（收车）】                     ture

    /* *****20170711追加***** */
    private int depend; //是否是自营司机(1-自营,2-加盟)
    private int appStatus; //1-前台,2-后台,3-锁屏

    /* *****20170809追加***** */
    private String dispatchUuid; //调度编号

    /* *****20170829追加***** */
    private int isNavigation; //1-导航点,2-定位点

    private int remindType;

    /* *****20211019追加***** */
    private long routeUid;
    private int routeIndex;
    private int routeRemainDistance;
    private int routeRemainTime;

    public UploadPosition() {

    }

    public UploadPosition(String adcode, String locationUuid, String driverUuid, String vehicleUuid, String vehLvUuid,
                          String orderUuid, String passengerUuid, Double distance, String lat, String lng, Double angle,
                          String appid, Double mileage, Long uploadTime, Integer orderStatus, Double speed,
                          Integer isListen, int depend, int appStatus, String dispatchUuid, int remindType) {
        this.adcode = adcode;
        this.locationUuid = locationUuid;
        this.driverUuid = driverUuid;
        this.vehicleUuid = vehicleUuid;
        this.vehLvUuid = vehLvUuid;
        this.orderUuid = orderUuid;
        this.passengerUuid = passengerUuid;
        this.distance = distance;
        this.lat = lat;
        this.lng = lng;
        this.angle = angle;
        this.appid = appid;
        this.mileage = mileage;
        this.uploadTime = uploadTime;
        this.orderStatus = orderStatus;
        this.speed = speed;
        this.isListen = isListen;
        this.depend = depend;
        this.appStatus = appStatus;
        this.dispatchUuid = dispatchUuid;
        this.remindType = remindType;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public String getDriverUuid() {
        return driverUuid;
    }

    public void setDriverUuid(String driverUuid) {
        this.driverUuid = driverUuid;
    }

    public String getVehicleUuid() {
        return vehicleUuid;
    }

    public void setVehicleUuid(String vehicleUuid) {
        this.vehicleUuid = vehicleUuid;
    }

    public String getVehLvUuid() {
        return vehLvUuid;
    }

    public void setVehLvUuid(String vehLvUuid) {
        this.vehLvUuid = vehLvUuid;
    }

    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }

    public String getLocationUuid() {
        return locationUuid;
    }

    public void setLocationUuid(String locationUuid) {
        this.locationUuid = locationUuid;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public Double getAngle() {
        return angle;
    }

    public void setAngle(Double angle) {
        this.angle = angle;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getPassengerUuid() {
        return passengerUuid;
    }

    public void setPassengerUuid(String passengerUuid) {
        this.passengerUuid = passengerUuid;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public Double getMileage() {
        return mileage;
    }

    public void setMileage(Double mileage) {
        this.mileage = mileage;
    }

    public Long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Long uploadTime) {
        this.uploadTime = uploadTime;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Integer getIsListen() {
        return isListen;
    }

    public void setIsListen(Integer isListen) {
        this.isListen = isListen;
    }

    public int getDepend() {
        return depend;
    }

    public void setDepend(int depend) {
        this.depend = depend;
    }

    public int getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(int appStatus) {
        this.appStatus = appStatus;
    }

    public String getDispatchUuid() {
        return dispatchUuid;
    }

    public void setDispatchUuid(String dispatchUuid) {
        this.dispatchUuid = dispatchUuid;
    }

    public int getIsNavigation() {
        return isNavigation;
    }

    public void setIsNavigation(int isNavigation) {
        this.isNavigation = isNavigation;
    }

    public int getRemindType() {
        return remindType;
    }

    public void setRemindType(int remindType) {
        this.remindType = remindType;
    }

    public long getRouteUid() {
        return routeUid;
    }

    public void setRouteUid(long routeUid) {
        this.routeUid = routeUid;
    }

    public int getRouteIndex() {
        return routeIndex;
    }

    public void setRouteIndex(int routeIndex) {
        this.routeIndex = routeIndex;
    }

    public int getRouteRemainDistance() {
        return routeRemainDistance;
    }

    public void setRouteRemainDistance(int routeRemainDistance) {
        this.routeRemainDistance = routeRemainDistance;
    }

    public int getRouteRemainTime() {
        return routeRemainTime;
    }

    public void setRouteRemainTime(int routeRemainTime) {
        this.routeRemainTime = routeRemainTime;
    }
}
