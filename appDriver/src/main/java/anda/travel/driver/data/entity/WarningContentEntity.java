package anda.travel.driver.data.entity;

/**
 * @Author: Jotyy
 * @Company: HeXing
 * @Date: 2019/7/16
 * @Desc:
 */
public class WarningContentEntity {

    /**
     * warnUuid : 1cc99b5705e7450cb0eab8666c66e52d
     * warnContent : 系统检车您接单数较少，请注意保持手机正常接单
     */

    private String warnUuid;
    private String warnContent;

    public String getWarnUuid() {
        return warnUuid;
    }

    public void setWarnUuid(String warnUuid) {
        this.warnUuid = warnUuid;
    }

    public String getWarnContent() {
        return warnContent;
    }

    public void setWarnContent(String warnContent) {
        this.warnContent = warnContent;
    }
}
