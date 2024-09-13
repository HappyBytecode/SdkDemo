package anda.travel.driver.data.entity;

import java.util.List;

public class JournalEntity {
    //司机UUID
    public String uuid;
    //总流水收入(元)
    public double totalIncome;
    //总奖励收入(元)
    public double rewardIncome;
    //订单总数目
    public int orderCount;
    //流水订单
    public List<Object> orders;//此字段新接口不用对接
    public List<JournalDetailEntity> details;
    //流水数
    public int number;
}
