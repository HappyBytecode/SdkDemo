package anda.travel.driver.data.entity;

/**
 * 检测结果
 */
public class CheckResultEntity {

    public String errorTitle;
    public String errorReason;

    public CheckResultEntity() {
    }

    public CheckResultEntity(String errorTitle, String errorReason) {
        this.errorTitle = errorTitle;
        this.errorReason = errorReason;
    }

}
