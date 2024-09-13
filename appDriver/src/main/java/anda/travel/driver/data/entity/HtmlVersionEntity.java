package anda.travel.driver.data.entity;

/**
 * @Author: Jotyy
 * @Company: HeXing
 * @Date: 2019/8/23
 * @Desc:
 */
public class HtmlVersionEntity {

    /**
     * updType : 1
     * packagePath : /dist/1.0.0/dist.zip
     * packageSize : 270
     * updPackagePath :
     * updPackageSize : null
     * versionNo : 1.0.0
     * updNote :
     * createTime : 1566374882000
     * platform : 2
     * moduleName : 排行榜
     * moduleCode : dist
     */

    private int updType;
    private String packagePath;
    private int packageSize;
    private String updPackagePath;
    private Object updPackageSize;
    private String versionNo;
    private String updNote;
    private long createTime;
    private int platform;
    private String moduleName;
    private String moduleCode;

    public int getUpdType() {
        return updType;
    }

    public void setUpdType(int updType) {
        this.updType = updType;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

    public int getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(int packageSize) {
        this.packageSize = packageSize;
    }

    public String getUpdPackagePath() {
        return updPackagePath;
    }

    public void setUpdPackagePath(String updPackagePath) {
        this.updPackagePath = updPackagePath;
    }

    public Object getUpdPackageSize() {
        return updPackageSize;
    }

    public void setUpdPackageSize(Object updPackageSize) {
        this.updPackageSize = updPackageSize;
    }

    public String getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(String versionNo) {
        this.versionNo = versionNo;
    }

    public String getUpdNote() {
        return updNote;
    }

    public void setUpdNote(String updNote) {
        this.updNote = updNote;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
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
}
