package anda.travel.driver.data.entity;

import anda.travel.driver.baselibrary.utils.TypeUtil;

/**
 * 支付类型 及 相关数据
 */
public class PayTypeEntity {

    public PayTypeEntity() {
    }

    public PayTypeEntity(String payType) {
        this.payType = payType;
    }

    private String payType; //支付方式
    private String data; //相关数据

    public String getPayType() {
        return TypeUtil.getValue(payType);
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
