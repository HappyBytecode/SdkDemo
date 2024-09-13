package anda.travel.driver.data.entity;

import java.io.Serializable;

public class TagEntity implements Serializable {
    /**
     * source 数量
     */
    private int source;

    /**
     * content 内容
     */
    private String content;
    /**
     * type 评价类型
     */
    private int type;

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

