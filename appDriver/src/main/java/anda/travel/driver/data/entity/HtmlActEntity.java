package anda.travel.driver.data.entity;

/**
 * @Author: Jotyy
 * @Company: HeXing
 * @Date: 2019/8/19
 * @Desc:
 */
public class HtmlActEntity {

    /**
     * uuid : 1b5fc3a98bac4b5786367546fd0c8394
     * moduleName : 排行榜
     * moduleCode : paihangbang
     * lastVersionNo : 1.2
     * remark :
     * createTime : 1566285164000
     */

    private String uuid;
    private String moduleName;
    private String moduleCode;
    private String lastVersionNo;
    private String remark;
    private long createTime;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getLastVersionNo() {
        return lastVersionNo;
    }

    public void setLastVersionNo(String lastVersionNo) {
        this.lastVersionNo = lastVersionNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
