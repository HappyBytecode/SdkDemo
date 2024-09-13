package anda.travel.driver.config;

/**
 * 功能描述：司机首页返回的状态值
 */
public class HxHomeStatus {

    /**
     * 状态值 - 1: 等待接驾超过出发时间
     */
    public final static int DRV_HOMEPAGE_STATUS_TIME_OUT = 1;
    /**
     * 状态值 - 2: 进行中的订单
     */
    public final static int DRV_HOMEPAGE_STATUS_DOGIN = 2;
    /**
     * 状态值 - 3: 即将开始的预约单（10分钟内）
     */
    public final static int DRV_HOMEPAGE_STATUS_APPO = 3;

    public final static int DRV_HOMEPAGE_STATUS_NO_ORDER = 9;

}
