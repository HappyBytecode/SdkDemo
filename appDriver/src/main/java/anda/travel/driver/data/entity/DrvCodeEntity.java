package anda.travel.driver.data.entity;

import java.io.Serializable;

/**
 * @Author: Jotyy
 * @Company: HeXing
 * @Date: 2019/4/24
 * @Desc:
 */
public class DrvCodeEntity implements Serializable {

    /**
     * title : 码上出发
     * url : http://f2f/{drvUuid}
     * desc : 扫描二维码即可下单乘车
     * needShare : 1
     */

    private String title;
    private String url;
    private String desc;
    private int needShare;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getNeedShare() {
        return needShare;
    }

    public void setNeedShare(int needShare) {
        this.needShare = needShare;
    }
}
