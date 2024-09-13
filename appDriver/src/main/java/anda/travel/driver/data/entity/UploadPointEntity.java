package anda.travel.driver.data.entity;

import org.litepal.crud.DataSupport;

import anda.travel.driver.common.AppConfig;
import anda.travel.driver.config.DutyStatus;
import anda.travel.driver.socket.message.UploadLocationMessage;

/**
 * 订单开始到结束的时候，传给服务器的点存入数据库
 */
public class UploadPointEntity extends DataSupport {

    private String appid; //安达AppKey
    private String locationUuid;//位置点uuid
    private String driverUuid;//司机uuid
    private String vehicleUuid; //车辆uuid
    private String vehLvUuid; //车型uuid

    private Double distance;//与上一点的距离
    private String adcode; //行政区域编码
    private Double lat;//纬度
    private Double lng;//经度
    private Double angle;//角度
    private double speed; //当前速度

    private String orderUuid;//订单uuid
    private String passengerUuid; //乘客uuid
    private Double mileage; //订单中累计距离
    private Long uploadTime; //上传时间（时间戳）
    private int orderStatus; //订单状态

    private int isListen = Integer.parseInt(DutyStatus.OFF_DUTY);

    /* *****20170711追加***** */
    private int depend; //是否是自营司机(1-自营,2-加盟)
    private int vehDepend; //车辆的归属（1-自由，2-挂靠）
    private int appStatus; //1-前台,2-后台,3-锁屏

    /* *****20170809追加***** */
    private String dispatchUuid; //调度编号

    /* *****20170829追加***** */
    private int isNavigation; //1-导航点,2-定位点

    /**
     * 听单模式
     */
    private int remindType;
    private int isUpload = 0;/////是否上传 1已传 0未传

    public UploadPointEntity(String locationUuid, String driverUuid, String vehicleUuid, String vehLvUuid,
                             Double distance, Double lat, Double lng, Double angle, double speed, String adcode,
                             int depend, int vehDepend, int appStatus, int isNavigation, int remindType) {
        super();
        this.appid = AppConfig.ANDA_APPKEY;
        this.vehicleUuid = vehicleUuid;
        this.vehLvUuid = vehLvUuid;
        this.locationUuid = locationUuid;
        this.driverUuid = driverUuid;
        this.distance = distance;
        this.lat = lat;
        this.lng = lng;
        this.angle = angle;
        this.speed = speed;
        this.adcode = adcode;
        this.depend = depend;
        this.vehDepend = vehDepend;
        this.appStatus = appStatus;
        this.isNavigation = isNavigation;
        this.remindType = remindType;
    }

    //////////上传的消息实体转化成数据库实体类
    public static UploadPointEntity upload2Entity(UploadLocationMessage msg) {
        UploadPointEntity uploadPointEntity = new UploadPointEntity(msg.getLocationUuid(), msg.getDriverUuid(), msg.getVehicleUuid(), msg.getVehLvUuid()
                , msg.getDistance(), msg.getLat(), msg.getLng(), msg.getAngle(), msg.getSpeed()
                , msg.getAdcode(), msg.getDepend(), msg.getVehDepend(), msg.getAppStatus(), msg.getIsNavigation(), msg.getRemindType());
        uploadPointEntity.setOrderUuid(msg.getOrderUuid());
        uploadPointEntity.setPassengerUuid(msg.getPassengerUuid());
        uploadPointEntity.setMileage(msg.getMileage());
        uploadPointEntity.setUploadTime(msg.getUploadTime());
        uploadPointEntity.setOrderStatus(msg.getOrderStatus());
        uploadPointEntity.setSpeed(msg.getSpeed());
        uploadPointEntity.setIsNavigation(msg.getIsNavigation());
        uploadPointEntity.setIsListen(msg.getIsListen());
        uploadPointEntity.setUploadTime(msg.getUploadTime());
        return uploadPointEntity;
    }

    public String getDriverUuid() {
        return driverUuid;
    }

    public void setDriverUuid(String driverUuid) {
        this.driverUuid = driverUuid;
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

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
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

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPassengerUuid() {
        return passengerUuid;
    }

    public void setPassengerUuid(String passengerUuid) {
        this.passengerUuid = passengerUuid;
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

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
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

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getIsListen() {
        return isListen;
    }

    public void setIsListen(int isListen) {
        this.isListen = isListen;
    }

    public int getDepend() {
        return depend;
    }

    public void setDepend(int depend) {
        this.depend = depend;
    }

    public int getVehDepend() {
        return vehDepend;
    }

    public void setVehDepend(int vehDepend) {
        this.vehDepend = vehDepend;
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

    public int getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(int isUpload) {
        this.isUpload = isUpload;
    }

}
