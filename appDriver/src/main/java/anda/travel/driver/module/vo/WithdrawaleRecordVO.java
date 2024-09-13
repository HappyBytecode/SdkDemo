package anda.travel.driver.module.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import anda.travel.driver.data.entity.WithdrawalRecordEntity;

/**
 * 提现记录
 */
public class WithdrawaleRecordVO {

    public static WithdrawaleRecordVO createFrom(WithdrawalRecordEntity entity) {
        if (entity == null) return new WithdrawaleRecordVO();
        String strJson = JSON.toJSONString(entity);
        return JSON.parseObject(strJson, WithdrawaleRecordVO.class);
    }

    @JSONField(name = "uuid")
    public String cashUuid;//id

    @JSONField(name = "cash")
    public double money;//费用

    @JSONField(name = "createTime")
    public long date;//时间

    @JSONField(name = "status")
    public int statue;//状态
}
