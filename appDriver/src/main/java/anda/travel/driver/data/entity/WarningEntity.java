package anda.travel.driver.data.entity;

/**
 * @Author: Jotyy
 * @Company: HeXing
 * @Date: 2019/7/16
 * @Desc:
 */
public class WarningEntity {

    /**
     * uuid : 9a828fcf3a7041108073f7cdfe654f03
     * driverUuid : DRV340101636c51d924b7a9bf753af45
     * warTime : 10:36:43
     * operator : 19a754bc782543d3a1016756e3a3f640
     * creatTime : 1563158203000
     * updateTime : 1563161545000
     * warnCount : 1
     * status : 2
     * acceptStatus : 2
     * warnContent : 您已挂机多时
     */

    private String uuid;
    private String driverUuid;
    private String warTime;
    private String operator;
    private long creatTime;
    private long updateTime;
    private int warnCount;
    private int status;
    private int acceptStatus;
    private String warnContent;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDriverUuid() {
        return driverUuid;
    }

    public void setDriverUuid(String driverUuid) {
        this.driverUuid = driverUuid;
    }

    public String getWarTime() {
        return warTime;
    }

    public void setWarTime(String warTime) {
        this.warTime = warTime;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public long getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(long creatTime) {
        this.creatTime = creatTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getWarnCount() {
        return warnCount;
    }

    public void setWarnCount(int warnCount) {
        this.warnCount = warnCount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAcceptStatus() {
        return acceptStatus;
    }

    public void setAcceptStatus(int acceptStatus) {
        this.acceptStatus = acceptStatus;
    }

    public String getWarnContent() {
        return warnContent;
    }

    public void setWarnContent(String warnContent) {
        this.warnContent = warnContent;
    }
}
