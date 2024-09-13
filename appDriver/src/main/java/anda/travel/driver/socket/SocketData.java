package anda.travel.driver.socket;

import anda.travel.driver.data.entity.OrderEntity;

/**
 * 功能描述:
 */
public class SocketData {

    public String orderUuid;

    public String mainOrderUuid;

    public OrderEntity orderDetailBean; //订单详情

    public String orderType;

    public Integer returnCode;

    public String msg;

    /* ***** 20170801追加 ***** */
    public Integer loops; //阶段数
    public Integer loopCnt; //阶段轮数

    /*20190107 增加服务端推送时 推送轮次增加是否已经结拼车单标识，如果本地判断已接拼车单，服务端推送未拼车单时，忽略该轮次推送*/
    public Integer status; //1. 未接拼车单 2.已接拼车单

    public String drvPointInterval;

    public Integer rewardFlag;//1就是有奖励标示
}
