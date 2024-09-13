package anda.travel.driver.data.entity;

import anda.travel.driver.baselibrary.utils.NumberUtil;

public class CashSettingEntity {

    public Integer isCash; //乘客是否可提现（1-可以、2-不可以）
    public Double balance; //司机余额
    public String remark; //提现说明
    public long month;//月份（自营司机有）

    public String getStrBalance() {
        return NumberUtil.getFormatValue(balance);
    }

    public String getRemark() {
        return remark;
    }

    public boolean isWithdrawClickable() {
        return isCash != null && isCash == 1;
    }

}
