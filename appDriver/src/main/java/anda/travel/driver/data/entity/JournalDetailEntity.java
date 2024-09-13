package anda.travel.driver.data.entity;

public class JournalDetailEntity {

    /**
     * uuid : a60937c70e812b5b3cf71fe8f03e2468
     * orderUuid : 6d16e2c309e04286bf5d3e67fcda673a
     * driverUuid : DRV000000dc76492974b24bc9e2d56d5
     * thirdSource : 1
     * thirdStatus : null
     * thirdActivityCode : null
     * rewardDetail : null
     * rewardMoneySum : 0.4
     * createTime : 1614736969000
     * updateTime : 1614736969000
     * type : 1
     * description : 订单收入
     * context : 车费
     * orderInfo : {}
     */

    private String uuid;
    private String orderUuid;
    private String driverUuid;
    private int thirdSource;
    private Object thirdStatus;
    private Object thirdActivityCode;
    private Object rewardDetail;
    private double rewardMoneySum;
    private long createTime;
    private long updateTime;
    private int type;
    private String description;
    private String context;
    private JournalOrderEntity orderInfo;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }

    public String getDriverUuid() {
        return driverUuid;
    }

    public void setDriverUuid(String driverUuid) {
        this.driverUuid = driverUuid;
    }

    public int getThirdSource() {
        return thirdSource;
    }

    public void setThirdSource(int thirdSource) {
        this.thirdSource = thirdSource;
    }

    public Object getThirdStatus() {
        return thirdStatus;
    }

    public void setThirdStatus(Object thirdStatus) {
        this.thirdStatus = thirdStatus;
    }

    public Object getThirdActivityCode() {
        return thirdActivityCode;
    }

    public void setThirdActivityCode(Object thirdActivityCode) {
        this.thirdActivityCode = thirdActivityCode;
    }

    public Object getRewardDetail() {
        return rewardDetail;
    }

    public void setRewardDetail(Object rewardDetail) {
        this.rewardDetail = rewardDetail;
    }

    public double getRewardMoneySum() {
        return rewardMoneySum;
    }

    public void setRewardMoneySum(double rewardMoneySum) {
        this.rewardMoneySum = rewardMoneySum;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public JournalOrderEntity getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(JournalOrderEntity orderInfo) {
        this.orderInfo = orderInfo;
    }

}
