package anda.travel.driver.client.constants;

/**
 * @author ZoroJ
 * @since 2017/3/17.
 */
public class OperateCode {

    public static final int ORDER_PRICE_CHANGE_TO_PASSENGER = 10301; //乘客价格变动

    public static final int ORDER_ABNORMAL = 10401; //安全提醒 异常订单,短单长跑

    public static final int ORDER_CROSS_CITY = 10402; //跨城订单

    //乘客操作需要推给司机的操作码 20推送司机
    public static final int ORDER_PUSH = 20201;//可抢订单推送
    public static final int ORDER_DISTRIBUTE = 20202;//订单派送
    public static final int ORDER_PASSENGER_CANCEL = 20203;//乘客取消订单
    public static final int ORDER_PASSENGER_ORDER_PAYED = 20204;//用户已支付
    public static final int ORDER_DISTRIBUTE_TO_OTHER = 20205;//订单被改派
    public static final int ORDER_CHANGE_DISTRIBUTE = 20206;//收到改派订单
    public static final int ORDER_PASSENGER_APPOTIME_REMIND = 20207;//预约提醒
    public static final int ORDER_PRICE_CHANGE_TO_DRIVER = 20301; //司机价格变动

    //20170809追加
    public static final int DISPATCH_START = 20401; //调度开始
    public static final int DISPATCH_END = 20402; //调度结束

    //系统推送司机
    public static final int ORDER_SYSTEM_CANCEL_TO_DRIVER = 30201;//订单取消
    public static final int POINT_INTERVAL = 30204; //推送打点频率

    public static final int ORDER_CHANGE_ADDRESS = 30701; //更改目的地系统推荐码

    public static final int ORDER_SYSTEM_DIRECTLY = 30501; //系统直接派单
    public static final int FEMALE_NIGHT_FORBIDDEN = 30601; //禁止女司机夜间出车推送

    //消息推送
    public static final int NOTICE_DRIVER = 40401;//系统消息推送

    public static final int FORCE_OFF_DUTY = 60001; //被强制收车
    public static final int FORCE_ORDER = 50001; //强制派单(一旦收到，订单已是改司机的)
    public static final int DISPATH_ORDER = 50002;  // 正式派单模式

    public static final int SYS_WARNING = 60011; //系统警告
    public static final int SYS_REMIND = 60020; //系统提醒

    public static final int GAO_DE_IN_SERVICE = 70001; //高德接单服务中
    public static final int GAO_DE_NOT_IN_SERVICE = 70002; //高德服务结束

    public static final int AWAKEN = 80001; //司机唤醒

    public static final int DISPATCH_REMIND = 20601; //提示调度

    //系统推送给司机的操作码
    public static final int SYSTEM_MESSAGE = 30401; //系统消息

}
