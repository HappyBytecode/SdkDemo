package anda.travel.driver.event;

import org.greenrobot.eventbus.EventBus;

public class OrderEvent extends BaseEvent {

    public static final int TAXI_UPDATE_FARE = 1; //(出租车)已输入费用
    public static final int LOCAL_CANCEL = 2; //司机取消订单
    public static final int PAY_SUCCESS = 3; //支付成功
    public static final int LAST_SPECIAL_INFO = 4; //获取最近一次"专车里程信息"的反馈报文
    public static final int SPECIAL_PRICE = 5; //专车订单金额
    public static final int DUTY_OREDER_ONGOING = 6; //有订单正在进行中
    public static final int ORDER_REFUSE = 11; //拒绝派单
    public static final int FORCE_ORDER = 12; //强制派单
    public static final int DISPATCH_ORDER = 13;

    public static final int PRICE_CHANGE = 100; //刷新订单详情

    //乘客操作需要推给司机的操作码
    public static final int ORDER_PUSH = 20201;//可抢订单推送
    public static final int ORDER_DISTRIBUTE = 20202;//订单派送
    public static final int ORDER_PASSENGER_CANCEL = 20203;//乘客取消订单
    public static final int ORDER_PASSENGER_ORDER_PAYED = 20204;//用户已支付
    public static final int ORDER_DISTRIBUTE_TO_OTHER = 20205;//订单被改派
    public static final int ORDER_CHANGE_DISTRIBUTE = 20206;//收到改派订单
    public static final int ORDER_PASSENGER_APPOTIME_REMIND = 20207;//预约提醒

//    public static final int ORDER_SENDTASK_NEW_APPLY = 2006;//乘客申请行程
//    public static final int ORDER_MATCH_SUCCESS = 2007; //订单匹配成功

    public static final int CLOSE_NAVI = 1111111;//关闭导航页
    public static final int ORDER_SYSTEM_DIRECTLY = 30501;

    public static final int ORDER_ABNORMAL = 10401; ////异常-短单长跑

    public static final int ORDER_CROSS_CITY = 10402; ////跨城订单
    public static final int ORDER_CHANGE_ADDRESS = 30701; //更改目的地系统推荐码

    /**
     * 发送消息
     */
    public void post() {
        EventBus.getDefault().post(this);
    }

    public OrderEvent(int type) {
        super(type);
    }

    public OrderEvent(int type, Object obj1) {
        super(type, obj1);
    }

    public OrderEvent(int type, Object obj1, Object obj2) {
        super(type, obj1, obj2);
    }

    public OrderEvent(int type, Object obj1, Object obj2, Object obj3) {
        super(type, obj1, obj2, obj3);
    }
}
