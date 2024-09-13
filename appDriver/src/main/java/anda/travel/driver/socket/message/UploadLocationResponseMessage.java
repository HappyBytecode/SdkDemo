package anda.travel.driver.socket.message;

import anda.travel.driver.socket.message.base.Message;

/**
 * UploadLocationResponseMessage 位置上传响应报文
 *
 * @author Zoro
 * @date 2016/12/28
 */
public class UploadLocationResponseMessage extends Message {

    private String locationUuid;
    private Boolean success;
    private String msg;
    private Double totalFare; //总价
    private String orderUuid; //订单编号
    private Integer errorCode; //错误码
    private String[] locationUuids;

    public String getLocationUuid() {
        return locationUuid;
    }

    public void setLocationUuid(String locationUuid) {
        this.locationUuid = locationUuid;
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

    public Double getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(Double totalFare) {
        this.totalFare = totalFare;
    }

    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String[] getLocationUuids() {
        return locationUuids;
    }

    public void setLocationUuids(String[] locationUuids) {
        this.locationUuids = locationUuids;
    }
}
