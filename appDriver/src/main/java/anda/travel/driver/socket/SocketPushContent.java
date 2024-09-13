package anda.travel.driver.socket;

/**
 * 功能描述：推送消息的content实体
 */
public class SocketPushContent {

    public String pushUuid; //推送编号

    public String to;

    public String appId;

    public Integer opCode; //对应的推送类型

    public SocketData data; //订单id在data中

    public String orderUuid; //特别说明：这层没有orderId，需要从data中取出

    public String title; //顶部通知title

    public String alert; //顶部通知content

    public String report; //语音播报内容

}
