package anda.travel.driver.data.ad;

import java.io.Serializable;

public class SplashAdEntity implements Serializable {
    public String uuid;
    public String adUuid;
    public int imgIndex;
    public int imgHeight;
    public int imgWidth;
    public String imgUrl;
    public String createTime;
    public String updateTime;
    public String updater;

    @Override
    public String toString() {
        return "SplashAdEntity{" +
                "uuid='" + uuid + '\'' +
                ", adUuid='" + adUuid + '\'' +
                ", imgIndex=" + imgIndex +
                ", imgHeight=" + imgHeight +
                ", imgWidth=" + imgWidth +
                ", imgUrl='" + imgUrl + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", updater='" + updater + '\'' +
                '}';
    }
}