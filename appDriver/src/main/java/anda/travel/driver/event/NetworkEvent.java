package anda.travel.driver.event;

public class NetworkEvent extends BaseEvent {

    public final static int CONNECT = 1; //网络连接
    public final static int DISCONNECT = 2; //网络中断

    public NetworkEvent(int type) {
        super(type);
    }

    public NetworkEvent(int type, Object obj1) {
        super(type, obj1);
    }

    public NetworkEvent(int type, Object obj1, Object obj2) {
        super(type, obj1, obj2);
    }

}