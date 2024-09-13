package anda.travel.driver.module.vo;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.model.LatLng;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import anda.travel.driver.config.HxCancelObject;
import anda.travel.driver.config.HxLateType;
import anda.travel.driver.config.OrderStatus;
import anda.travel.driver.config.TimeType;
import anda.travel.driver.data.entity.OrderCostItemEntity;
import anda.travel.driver.data.entity.OrderEntity;
import anda.travel.driver.baselibrary.utils.DateUtil;
import anda.travel.driver.baselibrary.utils.NumberUtil;
import anda.travel.driver.baselibrary.utils.TypeUtil;

/**
 * 功能描述：UI层使用的订单数据模型
 */
public class OrderVO implements Serializable {

    public static OrderVO createFrom(OrderEntity entity) {
        if (entity == null) return new OrderVO();
        String strJson = JSON.toJSONString(entity);
        return JSON.parseObject(strJson, OrderVO.class);
    }

    public Boolean isDistribute; //是否为派单（本地记录，非服务端返回）
    public Boolean isRedistribute; //是否为改派订单
    public Integer loops; // 20170801追加：阶段数（本地记录，长连接推送过来的）
    public Integer loopCnt; // 20170801追加：阶段轮数（本地记录，长连接推送过来的）
    public boolean ignoreCenterMileage; // 20170801追加：忽略上一点距离当前位置的里程（本地记录）
    public Double tempPrice; // 20170802追加：临时的价格，用于继续计费时的价格显示
    private Integer rewardFlag;//2020.12.29追加：美团奖励，用于显示

    public Double getTempPrice() {
        return tempPrice;
    }

    public void setTempPrice(Double tempPrice) {
        this.tempPrice = tempPrice;
    }

    public boolean getDistribute() {
        if (isDistribute == null) return false;
        return isDistribute;
    }

    public void setDistribute(Boolean distribute) {
        isDistribute = distribute;
    }

    public boolean getRedistribute() {
        if (isRedistribute == null) return false;
        return isRedistribute;
    }

    public void setRedistribute(Boolean redistribute) {
        isRedistribute = redistribute;
    }

    public Integer getLoops() {
        return loops;
    }

    public void setLoops(Integer loops) {
        this.loops = loops;
    }

    public Integer getLoopCnt() {
        return loopCnt;
    }

    public void setLoopCnt(Integer loopCnt) {
        this.loopCnt = loopCnt;
    }

    public Integer getRewardFlag() {
        return rewardFlag;
    }

    public void setRewardFlag(Integer rewardFlag) {
        this.rewardFlag = rewardFlag;
    }

    public boolean isIgnoreCenterMileage() {
        return ignoreCenterMileage;
    }

    public void setIgnoreCenterMileage(boolean ignoreCenterMileage) {
        this.ignoreCenterMileage = ignoreCenterMileage;
    }

    public long getDepartTime() {
        return TypeUtil.getValue(departTime);
    }

    public String getOriginAddress() {
        return TypeUtil.getValue(originAddress);
    }

    public String getDestAddress() {
        return TypeUtil.getValue(destAddress);
    }

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
    public PassengerVO passenger;
    public DriverVO driver; // null,
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
    public Integer abnormalStatus; //////0 正常 1 跨城 2 短单长跑

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

    //拼车UUID
    public String shareUuid;
    //拼车状态  1.已拼成 2.未拼成 3.不是拼车单
    public int shareStatus;
    public String source; /////订单来源：1 APP移动端；2 微信公众号；3 电话叫车；4 pc网站;5 扫码 6 一键下单
    public int routeId;//用户下单时选择路线id

    /**
     * 是否显示"迟到计费"
     *
     * @return
     */
    public boolean showLateBtn() {
        if (isLateBilling != null) {
            return isLateBilling == HxLateType.NORMAL
                    || isLateBilling == HxLateType.LATE;
        }
        return false;
    }

    /**
     * 是否显示
     *
     * @return
     */
    public boolean showCancelBtn() {
        if (subStatus != OrderStatus.WATI_PASSENGER_GET_ON) return false;

        return isLateBilling != null
                && (isLateBilling == HxLateType.LATE
                || isLateBilling == HxLateType.BILLING);
    }

    /**
     * 获取出发地坐标
     *
     * @return
     */
    public LatLng getOriginLatLng() {
        if (originLat == null || originLng == null
                || originLat == 0 || originLng == 0)
            return null;

        return new LatLng(originLat, originLng);
    }

    /**
     * 获取目的地坐标
     *
     * @return
     */
    public LatLng getDestLatLng() {
        if (destLat == null || destLng == null
                || destLat == 0 || destLng == 0)
            return null;

        return new LatLng(destLat, destLng);
    }

    /**
     * 获取乘客编号
     *
     * @return
     */
    public String getPassengerUuid() {
        if (passenger == null) return "";
        return TypeUtil.getValue(passenger.uuid);
    }

    /**
     * 获取乘客的联系电话
     *
     * @return
     */
    public String getPassengerPhone() {
        if (passenger == null) return "";
        return TypeUtil.getValue(passenger.mobile);
    }

    /**
     * 获取调度费
     *
     * @return
     */
    public String getStrTip() {
        if (tip == null || tip <= 0) return "";
        return NumberUtil.getFormatValue(tip, false);
    }

    /**
     * 出发时间：xx月xx日 xx:xx
     *
     * @return
     */
    public String getStrDepartTime() {
        if (departTime == null || departTime <= 0) return "";
        return DateUtil.formatDate(new Date(departTime), "yyyy年M月dd日 HH:mm");
    }

    /**
     * @return
     */
    public boolean isOrderOngoing() {
        if (subStatus == null) return false;
        //乘客已上车，前往目的地
        return subStatus == OrderStatus.WAIT_ARRIVE_ORIGIN //去接乘客
                || subStatus == OrderStatus.WATI_PASSENGER_GET_ON //到达目的地，等待乘客上车
                || subStatus == OrderStatus.DEPART; //进行中订单，需跳转OrderOngoingActivity
        //需跳转OrderDetailActivity
    }

    /**
     * @return
     */
    public boolean isOrderWaitBegin() {
        if (subStatus == null) return false;
        return subStatus == OrderStatus.WAIT_BEGIN_APPOINTMENT;
    }

    public boolean isCanceled() {
        return mainStatus == OrderStatus.ORDER_MAIN_STATUS_CANCEL;
    }

    /**
     * 获取取消方
     *
     * @return
     */
    public String getCancelObject() {
        if (cancelObject == null) return "";
        switch (cancelObject) {
            case HxCancelObject.CANCEL_BY_PASSENGER:
                return "乘客";
            case HxCancelObject.CANCEL_BY_DRIVER:
                return "司机";
            case HxCancelObject.CANCEL_BY_BACKEND:
                return "客服";
            case HxCancelObject.CANCEL_BY_SYSTEM:
                return "系统";
        }
        return "";
    }

    /**
     * 获取业务线id
     *
     * @return
     */
    public String getBusiUuid() {
        return TypeUtil.getValue(busiUuid);
    }

    /**
     * 获取"订单已推送给别人"的提示
     *
     * @return
     */
    public String getDistributeToOtherNotice() {
        StringBuilder str = new StringBuilder();
        str.append("您的订单 ");
        str.append(DateUtil.getTodayOrTomorrow(departTime));
        str.append("，从");
        str.append(getOriginAddress());
        str.append("到");
        str.append(getDestAddress());
        str.append("，已被成功改派");
        return str.toString();
    }

    /**
     * 是否为实时单
     *
     * @return
     */
    public boolean getIsRealtimeOrder() {
        return typeTime != null && typeTime == TimeType.REALTIME;
    }
}
