package anda.travel.driver.data.entity;

/**
 * 功能描述：首页订单状态
 */
public class OrderHomeStatusEntity {

    /**
     * 订单编号
     */
    public String orderUuid;

    /**
     * 业务状态值：
     * <p>
     * 1 正常，2 有进行中的订单，3 有待支付的订单，4 有预约单
     */
    public Integer status;

    /**
     * 订单类型：
     * <p>
     * 1 出租车，2 专车，3 拼车，4 接送机，5 日租/半日租
     */
    public Integer typeTrip;

}
