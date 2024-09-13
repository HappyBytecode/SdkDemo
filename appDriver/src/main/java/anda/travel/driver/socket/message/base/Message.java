package anda.travel.driver.socket.message.base;

import java.io.Serializable;

/**
 * Message
 *
 * @author Zoro
 * @date 2016/11/22
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private MessageType type;
    private String clientUuid;//必须唯一，否者会出现channel调用混乱

    public Message() {

    }

    public Message(MessageType type, String clientUuid) {
        this.type = type;
        this.clientUuid = clientUuid;
    }

    public String getClientUuid() {
        return clientUuid;
    }

    public void setClientUuid(String clientUuid) {
        this.clientUuid = clientUuid;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}

