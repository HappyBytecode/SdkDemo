package anda.travel.driver.data.entity;

import android.text.TextUtils;

import java.io.Serializable;

import anda.travel.driver.baselibrary.utils.TypeUtil;

public class NoticeEntity implements Serializable {

    private String uuid; // 主键
    private String title; // 公告标题
    private String content;    // 公告内容说明
    private String href; //	跳转地址
    private Integer type; // 类型（1：文本， 2：链接）
    private Integer status;    // 状态（1：可用，2：失效）
    private long createTime; // 创建时间

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return TypeUtil.getValue(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return TypeUtil.getValue(content);
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHref() {
        if (TextUtils.isEmpty(href)) return "";
        if (href.startsWith("www")) return "https://" + href;
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

}
