package anda.travel.driver.data.entity;

import anda.travel.driver.baselibrary.utils.NumberUtil;

public class MyWalletEntity {

    public String driverAccountUuid;//司机账户编号
    public double sunMoney;//钱包余额
    public double availableMoney;//可提现余额
    public double notAvailableMoney;//待入账金额
    public int status;//是否可以提现（1：可以；0：不可以）
    public String title;//提现说明
    public int isNewBill;//是否存在账单未支付（1：存在；0：不存在）
    public String remark;//备注，不可提现原因

    public String getStrBalance() {
        return NumberUtil.getFormatValue(sunMoney);
    }

    public String getRemark() {
        return remark;
    }

    public boolean isWithdrawClickable() {
        return status == 1;
    }

}
