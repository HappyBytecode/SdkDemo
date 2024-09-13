package anda.travel.driver.config;

/**
 * 功能描述：订单状态
 */
public interface OrderStatus {

    /**
     * 订单大状态
     **/
    //新建订单
    int ORDER_MAIN_STATUS_INITIAL = 10;
    //订单进行中
    int ORDER_MAIN_STATUS_DOING = 20;
    //行程结束、待支付
    int ORDER_MAIN_STATUS_DONE = 30;
    //支付完成、订单结束
    int ORDER_MAIN_STATUS_PAYED = 40;
    //订单取消（完结）
    int ORDER_MAIN_STATUS_CANCEL = 90;

    /**
     * 订单子状态
     **/
    int WAIT_REPLY = 10100; // 乘客发单、等待应答

    int WAIT_BEGIN_APPOINTMENT = 20100; // 等待接驾－预约订单
    int WAIT_ARRIVE_ORIGIN = 20200; // 等待接驾-已出发未到达"上车地点"
    int WATI_PASSENGER_GET_ON = 20300; // 等待接驾-已到达"上车地点"
    int DEPART = 20400; // 行程开始

    int ARRIVE_DEST = 20500; // 到达目的地

    int CLOSE = 90301; //已关闭订单

    int RISK_STATUS_YES = 1;//风险单
    int RISK_STATUS_NO = 2;//非风险单

    int ROUTING_DEFAULT = 0;//全部订单
    int ROUTING_CREATE = 10;//行程创建（订单分配中）
    int ROUTING_SERVICING = 20;//行程进行中（待服务）
    int UNPAID = 30;//确认费用（待支付）
    int ROUTING_END = 40;//行程完结（已支付）
    int ROUTING_CANCEL = 90;//行程取消
    int CAN_HURRY_PAY = 1;//可催付状态
    int CANNOT_HURRY_PAY = 0;//不可催付状态

    int REWARD_FLAG = 1;//美团奖励标识
}
