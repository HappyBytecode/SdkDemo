package anda.travel.driver.module.vo;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import anda.travel.driver.config.BillType;
import anda.travel.driver.data.entity.BillItemEntity;

public class BillIitemVO {

    public static BillIitemVO createFrom(BillItemEntity entity) {
        if (entity == null) return new BillIitemVO();
        String strJson = JSON.toJSONString(entity);
        return JSON.parseObject(strJson, BillIitemVO.class);
    }

    public int type;//收入/支持项目名称

    @JSONField(name = "createTime")
    public long time;//时间

    public double money;//费用

    public String remark;

    public String getRemark() {
        if (!TextUtils.isEmpty(remark)) return remark;
        String strRemark;
        switch (type) {
            case BillType.BILL_INCOME:
                strRemark = "收入";
                break;
            case BillType.BILL_PAY:
                strRemark = "支付";
                break;
            case BillType.BILL_RECHARGE:
                strRemark = "充值";
                break;
            case BillType.BILL_WITHDRAW:
                strRemark = "提现";
                break;
            case BillType.BILL_PUMP:
                strRemark = "抽成";
                break;
            default:
                strRemark = "";
                break;
        }
        return strRemark;
    }

}
