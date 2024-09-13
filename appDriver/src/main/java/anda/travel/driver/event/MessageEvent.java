package anda.travel.driver.event;

public class MessageEvent extends BaseEvent {

    public final static int NEW = 1; //有新消息
    public final static int CLEAR = 2; //清空消息
    public final static int REMIND = 3; //有预约单提醒
    public final static int SYS_WARNING = 4; //系统警告
    public static final int SYS_WARNING_READ = 5; //系统警告已读
    public static final int ACT_HTML_ON = 6; //h5活动入口
    public static final int SYS_MSG = 7; //系统消息

    public MessageEvent(int type) {
        super(type);
    }

    public MessageEvent(int type, Object obj1) {
        super(type, obj1);
    }

    public MessageEvent(int type, Object obj1, Object obj2) {
        super(type, obj1, obj2);
    }

}
