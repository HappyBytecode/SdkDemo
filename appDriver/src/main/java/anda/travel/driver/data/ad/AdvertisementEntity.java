package anda.travel.driver.data.ad;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ling on 2017/4/10.
 */

public class AdvertisementEntity implements Parcelable, Serializable {

    public AdvertisementEntity() {
    }

    /**
     * uuid : AD000000000000000000000000000001
     * imgUrl :
     * href :
     * status : 1
     * createTime : 1490786668000
     * updateTime : 1490786668000
     * updater : SYSTEM
     * appid : c543eae4aa0a49fdab3ed761f6345001
     * lastUpdTime : null
     */
    public static final String AUTO_PILOT = "autopilot";
    private int type;
    private String uuid;
    private String imgUrl;
    private String href;
    private int status;
    private long createTime;
    private long updateTime;
    private String updater;
    private String appid;
    private long lastUpdTime;
    private String title;
    private String content;

    public List<SplashAdEntity> getSysAdvertisementImageList() {
        return sysAdvertisementImageList;
    }

    public void setSysAdvertisementImageList(List<SplashAdEntity> sysAdvertisementImageList) {
        this.sysAdvertisementImageList = sysAdvertisementImageList;
    }

    private List<SplashAdEntity> sysAdvertisementImageList;

    public void setIosImgUrl(String iosImgUrl) {
        this.iosImgUrl = iosImgUrl;
    }

    private String iosImgUrl;

    public String getBusiType() {
        return busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }

    private String busiType;

    public String getAdCode() {
        return adCode;
    }

    public void setAdCode(String adCode) {
        this.adCode = adCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String adCode;
    /**
     * 点击广告跳转类型（1：页面，2：链接）
     **/
    private int jumpType;

    /**
     * （1：分享 ，2：充值， 3：建议反馈）
     **/
    private int pageType;

    protected AdvertisementEntity(Parcel in) {
        type = in.readInt();
        uuid = in.readString();
        imgUrl = in.readString();
        href = in.readString();
        status = in.readInt();
        createTime = in.readLong();
        updateTime = in.readLong();
        updater = in.readString();
        appid = in.readString();
        lastUpdTime = in.readLong();
        title = in.readString();
        jumpType = in.readInt();
        pageType = in.readInt();
        content = in.readString();
    }

    public static final Creator<AdvertisementEntity> CREATOR = new Creator<AdvertisementEntity>() {
        @Override
        public AdvertisementEntity createFromParcel(Parcel in) {
            return new AdvertisementEntity(in);
        }

        @Override
        public AdvertisementEntity[] newArray(int size) {
            return new AdvertisementEntity[size];
        }
    };

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public long getLastUpdTime() {
        return lastUpdTime;
    }

    public void setLastUpdTime(long lastUpdTime) {
        this.lastUpdTime = lastUpdTime;
    }

    public int getJumpType() {
        return jumpType;
    }

    public void setJumpType(int jumpType) {
        this.jumpType = jumpType;
    }

    public int getPageType() {
        return pageType;
    }

    public void setPageType(int pageType) {
        this.pageType = pageType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeString(this.uuid);
        dest.writeString(this.imgUrl);
        dest.writeString(this.href);
        dest.writeInt(this.status);
        dest.writeLong(this.createTime);
        dest.writeLong(this.updateTime);
        dest.writeString(this.updater);
        dest.writeString(this.appid);
        dest.writeLong(this.lastUpdTime);
        dest.writeString(this.title);
        dest.writeInt(this.jumpType);
        dest.writeInt(this.pageType);
        dest.writeString(this.content);
    }

    @Override
    public String toString() {
        return "AdvertisementEntity{" +
                "type=" + type +
                ", uuid='" + uuid + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", href='" + href + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", updater='" + updater + '\'' +
                ", appid='" + appid + '\'' +
                ", lastUpdTime=" + lastUpdTime +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", busiType='" + busiType + '\'' +
                ", adCode='" + adCode + '\'' +
                ", jumpType=" + jumpType +
                ", pageType=" + pageType +
                '}';
    }

    public String getIosImgUrl() {
        return iosImgUrl;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof AdvertisementEntity
                && !TextUtils.isEmpty(getUuid())
                && getUuid().equals(((AdvertisementEntity) obj).getUuid())) {
            return true;
        }
        return false;
    }
}
