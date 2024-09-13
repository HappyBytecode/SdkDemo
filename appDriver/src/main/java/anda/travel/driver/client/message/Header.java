package anda.travel.driver.client.message;

import java.util.UUID;

/**
 * Header
 *
 * @author Zoro
 * @date 2017/3/27
 */

public class Header {

    private int messageType;
    private String messageId;
    private String appid;
    private String clientId;
    private long timeMillis;

    public Header() {

    }

    public Header(int messageType, String appid, String clientId) {
        this.messageType = messageType;
        this.messageId = UUID.randomUUID().toString().replaceAll("-", "");
        this.appid = appid;
        this.clientId = clientId;
        this.timeMillis = System.currentTimeMillis();
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }

}
