package anda.travel.driver.data.entity;

public class JournalOrderEntity {
    //订单UUID
    public String uuid;
    //起点
    public String originAddress;
    //目的地地址
    public String destAddress;
    //订单类型（行程）：2 出租车；1 专车
    public int typeTrip;
    //订单总费用（=司机收入=各费用之和*高峰溢价率+附加服务费等+（后台）调整价格）
    public double totalFare;

    public String vehicleNo;

    public String driverName;

    public int subStatus;

    public int mainStatus;

}
