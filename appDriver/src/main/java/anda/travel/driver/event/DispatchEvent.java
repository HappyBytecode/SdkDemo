package anda.travel.driver.event;

public class DispatchEvent extends BaseEvent {

    public final static int DISPATCH_NaviInfoUpdate = 1; //(调度)导航信息更新
    public final static int DISPATCH_Refresh = 2; //刷新调度信息
    public final static int DISPATCH_COMPLETE = 3;  ///调度完成
    public final static int DISPATCH_REMIND = 4;  ///调度弹框提示用户是否去调度

    public DispatchEvent(int type) {
        super(type);
    }

    public DispatchEvent(int type, Object obj1) {
        super(type, obj1);
    }

    public DispatchEvent(int type, Object obj1, Object obj2) {
        super(type, obj1, obj2);
    }

}
