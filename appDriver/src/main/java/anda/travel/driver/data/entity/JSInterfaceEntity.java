package anda.travel.driver.data.entity;

/**
 * @Author: Jotyy
 * @Company: HeXing
 * @Date: 2019/9/17
 * @Desc: JS交互传参实体类
 */
public class JSInterfaceEntity {
    private String appid;
    private String uuid;
    private String token;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
