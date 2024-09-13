package anda.travel.driver.module.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import anda.travel.driver.data.entity.DriverEntity;

public class WithdrawalVO {

    public static WithdrawalVO createFrom(DriverEntity entity) {
        if (entity == null) return new WithdrawalVO();
        String strJson = JSON.toJSONString(entity);
        return JSON.parseObject(strJson, WithdrawalVO.class);
    }

    @JSONField(name = "actualName")
    public String driverName; //司机姓名

    @JSONField(name = "balance")
    public double balance; //余额

    @JSONField(name = "onCashAmount")
    public double audit_money; //审核中提现金额

    @JSONField(name = "canCashAmount")
    public Double canCashAmount; //可提现金额
}
