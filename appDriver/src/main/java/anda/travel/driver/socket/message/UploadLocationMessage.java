package anda.travel.driver.socket.message;

import anda.travel.driver.common.AppConfig;
import anda.travel.driver.config.DutyStatus;
import anda.travel.driver.data.entity.UploadPointEntity;
import anda.travel.driver.socket.message.base.Message;
import anda.travel.driver.socket.message.base.MessageType;

/**
 * UploadLocationMessage
 *
 * @author Zoro
 * @date 2016/12/28
 */
public class UploadLocationMessage extends Message {

    public UploadLocationMessage() {
        super();
        super.setType(MessageType.UPLOAD_LOCATION);
        this.appid = AppConfig.ANDA_APPKEY;
    }

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

    public UploadLocationMessage(String locationUuid, String driverUuid, String vehicleUuid, String vehLvUuid,
                                 Double distance, Double lat, Double lng, Double angle, double speed, String adcode,
                                 int depend, int vehDepend, int appStatus, int isNavigation, int remindType) {
        super();
        super.setType(MessageType.UPLOAD_LOCATION);
        this.appid = AppConfig.ANDA_APPKEY;
        //this.driverType = AppConfig.getType();
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

    ////数据库实体
    public static UploadLocationMessage entity2Msg(UploadPointEntity pointEntity) {
        UploadLocationMessage uploadLocationMessage = new UploadLocationMessage(pointEntity.getLocationUuid(), pointEntity.getDriverUuid(), pointEntity.getVehicleUuid()
                , pointEntity.getVehLvUuid(), pointEntity.getDistance(), pointEntity.getLat(), pointEntity.getLng(), pointEntity.getAngle()
                , pointEntity.getSpeed(), pointEntity.getAdcode(), pointEntity.getDepend(), pointEntity.getVehDepend(), pointEntity.getAppStatus()
                , pointEntity.getIsNavigation(), pointEntity.getRemindType());
        uploadLocationMessage.setOrderUuid(pointEntity.getOrderUuid());
        uploadLocationMessage.setPassengerUuid(pointEntity.getPassengerUuid());
        uploadLocationMessage.setMileage(pointEntity.getMileage());
        uploadLocationMessage.setOrderStatus(pointEntity.getOrderStatus());
        uploadLocationMessage.setSpeed(pointEntity.getSpeed());
        uploadLocationMessage.setIsNavigation(pointEntity.getIsNavigation());
        uploadLocationMessage.setIsListen(pointEntity.getIsListen());
        uploadLocationMessage.setUploadTime(pointEntity.getUploadTime());
        return uploadLocationMessage;
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
}
