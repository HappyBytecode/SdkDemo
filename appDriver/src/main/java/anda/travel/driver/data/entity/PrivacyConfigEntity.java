package anda.travel.driver.data.entity;

/**
 * Created by liuwenwu on 2021/11/26.
 * Des : 隐私更新配置项
 */
public class PrivacyConfigEntity {
    private String version;
    private String context;
    private String url;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
