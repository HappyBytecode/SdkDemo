package anda.travel.driver.client.message.body;

import anda.travel.driver.client.message.Body;

/**
 * RespPush
 *
 * @author Zoro
 * @date 2017/3/28
 */

public class RespPush implements Body {

    private String associatedMessageId;//关联消息ID

    public RespPush() {

    }

    public RespPush(String associatedMessageId) {
        this.associatedMessageId = associatedMessageId;
    }

    public String getAssociatedMessageId() {
        return associatedMessageId;
    }

    public void setAssociatedMessageId(String associatedMessageId) {
        this.associatedMessageId = associatedMessageId;
    }
}
