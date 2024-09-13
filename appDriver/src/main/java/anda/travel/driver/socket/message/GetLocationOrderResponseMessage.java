package anda.travel.driver.socket.message;

import anda.travel.driver.socket.message.base.Message;
import anda.travel.driver.baselibrary.utils.TypeUtil;

/**
 * 功能描述：
 */
public class GetLocationOrderResponseMessage extends Message {

    private String orderUuid;

    private Boolean success;

    private String msg;

    private Double lng;

    private Double lat;

    private Double mileage;

    private Long uploadTime;

    private Double totalFare; //总价

    public String getOrderUuid() {
        return TypeUtil.getValue(orderUuid);
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public double getMileage() {
        return TypeUtil.getValue(mileage);
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

    public Double getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(Double totalFare) {
        this.totalFare = totalFare;
    }

    @Override
    public String toString() {
        return "GetLocationOrderResponseMessage{" +
                "orderUuid='" + orderUuid + '\'' +
                ", success=" + success +
                ", msg='" + msg + '\'' +
                ", lng=" + lng +
                ", lat=" + lat +
                ", mileage=" + mileage +
                ", uploadTime=" + uploadTime +
                ", totalFare=" + totalFare +
                '}';
    }
}
