package anda.travel.driver.module.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import anda.travel.driver.data.entity.WithdrawalDetailsEntity;

public class WithdrawDetailVO {

    public static WithdrawDetailVO createFrom(WithdrawalDetailsEntity entity) {
        if (entity == null) return new WithdrawDetailVO();
        String strJson = JSON.toJSONString(entity);
        return JSON.parseObject(strJson, WithdrawDetailVO.class);
    }

    @JSONField(name = "cash")
    public double withdrawalFee;//提现费用

    @JSONField(name = "collectType")
    public int withrawalWay;//提现方式

    @JSONField(name = "collectName")
    public String withdrawalAccountName;//提现账户姓名

    @JSONField(name = "collectAccount")
    public String withdrawalAccount;//提现账户

    @JSONField(name = "")
    public Double poundage;//手续费

    @JSONField(name = "createTime")
    public Long applyTime;//申请时间

    @JSONField(name = "processTime")
    public Long auditTime;//审核时间

    @JSONField(name = "status")
    public Integer auditResult;//审核结果

    @JSONField(name = "reason")
    public String remark;//备注

    public Double cashFee; //手续费
}
