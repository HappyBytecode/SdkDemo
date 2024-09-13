package anda.travel.driver.data.entity;

import android.text.TextUtils;

public class UpgradeEntity {

    private boolean update;
    private boolean isUsed; ////false的时候需要强制更新
    private String isReview;
    private String updContent;
    private String updUrl;
    private int fileSize;
    private String versionName;
    private String updTitle;

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public String getIsReview() {
        return isReview;
    }

    public void setIsReview(String isReview) {
        this.isReview = isReview;
    }

    public String getUpdContent() {
        return updContent;
    }

    public void setUpdContent(String updContent) {
        this.updContent = updContent;
    }

    public String getUpdUrl() {
        return updUrl;
    }

    public void setUpdUrl(String updUrl) {
        this.updUrl = updUrl;
    }

    public int getFileSize() {
        return fileSize / 1024 / 1024;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getUpdTitle() {
        return TextUtils.isEmpty(updTitle) ? "更新" : updTitle;
    }

    public void setUpdTitle(String updTitle) {
        this.updTitle = updTitle;
    }

}
