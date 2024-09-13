package anda.travel.driver.socket;

import anda.travel.driver.client.message.AndaMessage;

public interface ISocket {

    boolean isSocketOpen(); //长连接是否开启

    void connectSocket() throws Exception; //开启长连接

    void closeSocket(); //关闭长连接

    void sendMessage(String msg); //发送消息

    void sendMessage(AndaMessage msg); //发送消息

    boolean timerOperation(); //定时操作 (如果返回true，表示正常；返回false，表示长时间没收到推送，需要重连)

    void setSocketListener(ISocketListener listener); //设置监听

}
