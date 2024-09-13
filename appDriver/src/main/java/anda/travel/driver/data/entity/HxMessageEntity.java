package anda.travel.driver.data.entity;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 功能描述：系统消息
 */
public class HxMessageEntity extends DataSupport implements Serializable {

    private static final long serialVersionUID = -5165400475238880740L;

    private String clientUuid; //用户uuid
    private String uuid; // String	主键
    private Integer type; // Integer	消息类型 （1.系统消息 、2.反馈回复、3.活动相关，4.订单相关、5.账单相关）
    private String pushType; // Integer	推送类型（1.文本、2.链接）

    private String orderUuid; //订单编号
    private String typeStr; //前端显示的"消息类型"
    private String title; // String	消息标题
    private String content; // String	消息内容
    private String linkUrl; // String	消息链接
    private String pic; // String	活动图片

    private Long pushTime; // Timestamp	推送时间
    private Long readTime; // Timestamp	读取时间
    private Integer status; // Integer	消息状态（1:未读、2:已读 3删除）

    private String report; // 优先播报该内容，如果为null或""，则使用content播报

    /**
     * 消息类型 （1.系统消息 、2.反馈回复、3.订单相关）
     */
    public static class MsgType {
        public final static int SYSTEM = 1;
        public final static int REPLY = 2;
        public final static int ORDER = 3;
    }

    /**
     * 推送类型（1.文本、2.链接）
     */
    public static class PushType {
        public final static int TEXT = 1;
        public final static int LINK = 2;
    }

    /**
     * 消息状态（1:未读、2:已读 3删除）
     */
    public static class ReadStatus {
        public final static int UNREAD = 1;
        public final static int READED = 2;
        public final static int DELETE = 3;
    }

    public String getClientUuid() {
        return clientUuid;
    }

    public void setClientUuid(String clientUuid) {
        this.clientUuid = clientUuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }

    public String getTypeStr() {
        return typeStr;
    }

    public void setTypeStr(String typeStr) {
        this.typeStr = typeStr;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public Long getPushTime() {
        return pushTime;
    }

    public void setPushTime(Long pushTime) {
        this.pushTime = pushTime;
    }

    public Long getReadTime() {
        return readTime;
    }

    public void setReadTime(Long readTime) {
        this.readTime = readTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    @Override
    public String toString() {
        return "HxMessageEntity{" +
                "clientUuid='" + clientUuid + '\'' +
                ", uuid='" + uuid + '\'' +
                ", type=" + type +
                ", pushType='" + pushType + '\'' +
                ", orderUuid='" + orderUuid + '\'' +
                ", typeStr='" + typeStr + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", linkUrl='" + linkUrl + '\'' +
                ", pic='" + pic + '\'' +
                ", pushTime=" + pushTime +
                ", readTime=" + readTime +
                ", status=" + status +
                ", report='" + report + '\'' +
                '}';
    }

}
