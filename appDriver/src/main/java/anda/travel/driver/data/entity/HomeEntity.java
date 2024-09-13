package anda.travel.driver.data.entity;

import anda.travel.driver.baselibrary.utils.NumberUtil;

/**
 * 首页信息
 */
public class HomeEntity {

    public Double income;
    public Integer countComplete; // 成单数
    public Long onWorkTime;
    public Long systemTime;
    public OrderEntity orderDetailBean;
    public DispatchEntity driverDispatchLog;
    public String onlineTime;
    public String onlineMileage;

    public String getStrOrderIncome() {
        return NumberUtil.getFormatValue(income);
    }
}
