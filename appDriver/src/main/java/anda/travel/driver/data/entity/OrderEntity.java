package anda.travel.driver.data.entity;

import java.io.Serializable;
import java.util.List;

import anda.travel.driver.config.OrderStatus;
import anda.travel.driver.baselibrary.utils.TypeUtil;

/**
 * 功能描述：订单信息
 */
public class OrderEntity implements Serializable {

    /* ****************************************************** */

    public String uuid; // "715667b6305d42988905fe10ecfbbdd8",
    public Integer subStatus; // 100,
    public String passengerCom; // null,
    public String driverCom; // null,
    public String originAddressDetail; // "软件园二期",
    public String originAddress; // "软件园二期",
    public Double originLng; // "118.182171",
    public Double originLat; // "24.483892",
    public String destAddressDetail; // "龙岩火车站",
    public String destAddress; // "龙岩火车站",
    public Double destLng; // "117.006442",
    public Double destLat; // "25.095996",
    public Integer typeTime; // 1,
    public Integer typeTrip; // 1,
    public Double totalFare; // null,
    public Long departTime; // 1483945874000,
    public int actualPassNum; // null,
    public PassengerEntity passenger;
    public DriverEntity driver; // null,
    public String comment; // null,
    public Double tip; // 10,
    public String remark; // null,
    public String cancelReason; // null,
    public String actualFare; // null,
    public String couponFare; // null,
    public String payType; // null,
    public String countdown; // -2289901,
    public int overTime; // 0,
    public String report; // "空驶费10元,从软件园二期到龙岩火车站",
    public String carModelsLevelUuid; // null,
    public String waitDuration; // null,
    public String income; // 0,
    public String orderCount; // null
    public List<OrderCostItemEntity> orderCostItemBean;
    public Integer cancelObject; // 取消方
    public String busiUuid; //业务线id

    // 2017-02-25 追加字段
    /**
     * 订单主状态
     */
    public Integer mainStatus;
    /**
     * （预约单）司机是否可以出发去接乘客（0：不可以；1：可以）
     */
    public Integer canPickUp;
    /**
     * 司机是否可以催促乘客付款（0：不可以；1：可以）
     */
    public Integer canHurryPay;

    public Integer isPumping; //是否可代付，是否有抽成（1:开代付、2:不开代付,无抽成、3:不开代付,有抽成）
    public Double pumpinFare; //抽成金额
    public Integer isDenominated; //是否需要"计算里程计价"

    public Integer isLateBilling;  //是否可迟到计费  1. 不可以，2. 可以
    public Long lateTime; //乘客迟到时间

    // 2017-04-19 追加字段
    public Double drvTotalFare; //扣除"抽成"后的收入

    // 2017-04-27 追加字段
    public Integer payStatus; //支付状态
    public Long freeWaitTime; //免费等待时间

    // 2017-05-04 追加字段
    public Long realLateTime;

    public int isVrPhoneNum;//是否有小号(1：有， 2：没有)
    public int vrPhoneExprTime;// 小号过期时间 (小时)

    // 2017-08-08 追加字段
    public Double drvArrLng;
    public Double drvArrLat;

    // 2018-03-07 追加字段
    public Double subsidyFare; //补贴金额

    //2018-11-23 添加是否为拼车
    public int carpool;//1.拼车单 2.非拼车单
    public String shareUuid;//拼车单uuid
    //拼车状态  1.已拼成 2.未拼成 3.不是拼车单
    public int shareStatus;
    public Integer source;
    public Integer abnormalStatus; //////0 正常 1 跨城 2 短单长跑
    public int routeId;//用户下单时选择路线id

    public boolean isOrderOngoing() {
        if (subStatus == null) return false;
        if (subStatus == OrderStatus.WAIT_ARRIVE_ORIGIN //去接乘客
                || subStatus == OrderStatus.WATI_PASSENGER_GET_ON //到达目的地，等待乘客上车
                || subStatus == OrderStatus.DEPART) //乘客已上车，前往目的地
            return true; //进行中订单，需跳转OrderOngoingActivity

        return false; //需跳转OrderDetailActivity
    }

    /**
     * 获取业务线id
     *
     * @return
     */
    public String getBusiUuid() {
        return TypeUtil.getValue(busiUuid);
    }

}
