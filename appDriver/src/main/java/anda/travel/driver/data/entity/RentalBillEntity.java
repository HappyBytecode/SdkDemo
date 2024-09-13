package anda.travel.driver.data.entity;

import java.io.Serializable;

/**
 * Created by liuwenwu on 2021/12/15.
 * Des : 租金账单实体
 */
public class RentalBillEntity implements Serializable {
    /**
     * "uuid":"02f56bd040e84b7e9ce3aca333271cd7",
     * "billNo":null,
     * "billName":null,
     * "billNumber":0,
     * "mobile":"17755119628",
     * "driverName":null,
     * "idcard":null,
     * "vehicleNo":null,
     * "type":null,
     * "billType":1,
     * "carType":null,
     * "money":0.01,
     * "status":0,
     * "dueDate":null,
     * "payType":null,
     * "payTime":null,
     * "payCertificate":null,
     * "createTime":null,
     * "updater":null,
     * "updateTime":null,
     * "billTypeStr":"租车押金"
     */

    private String uuid;//返回账单uuid
    private String billNo;
    private String billName;
    private String billNumber;//账单周期
    private String mobile;
    private String driverName;
    private String idcard;
    private String vehicleNo;
    private Integer type;
    private int billType;//1-押金2-租金
    private String carType;
    private String money;//支付金额
    private Integer status;//支付状态0-未支付 1-已支付
    private String dueDate;
    private String payType;
    private String payTime;//支付时间
    private String payCertificate;
    private String createTime;
    private String updater;
    private String updateTime;
    private String billTypeStr;//列表标签
    private String remark;//账单备注
    private boolean canPay;//true:可以支付；false：不可以支付

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getBillName() {
        return billName;
    }

    public void setBillName(String billName) {
        this.billName = billName;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public int getBillType() {
        return billType;
    }

    public void setBillType(int billType) {
        this.billType = billType;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getPayCertificate() {
        return payCertificate;
    }

    public void setPayCertificate(String payCertificate) {
        this.payCertificate = payCertificate;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getBillTypeStr() {
        return billTypeStr;
    }

    public void setBillTypeStr(String billTypeStr) {
        this.billTypeStr = billTypeStr;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean getCanPay() {
        return canPay;
    }

    public void setCanPay(boolean canPay) {
        this.canPay = canPay;
    }
}
