package anda.travel.driver.data.entity;

import java.io.Serializable;

/**
 * Created by yongxu on 2022/6/6.
 * Des : 租金账单信息实体
 */
public class RentBillInfoEntity implements Serializable {
    /**
     * "isPay": 0,
     * "sum": 0,
     * "isNotPay": 0
     */
    private int isPay;//已支付账单期数
    private int sum;//账单总期数
    private int isNotPay;//未支付账单期数

    public int getIsPay() {
        return isPay;
    }

    public void setIsPay(int isPay) {
        this.isPay = isPay;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public int getIsNotPay() {
        return isNotPay;
    }

    public void setIsNotPay(int isNotPay) {
        this.isNotPay = isNotPay;
    }
}
