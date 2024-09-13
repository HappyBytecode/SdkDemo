package anda.travel.driver.module.vo;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import anda.travel.driver.data.entity.OrderSummaryEntity;
import anda.travel.driver.baselibrary.utils.NumberUtil;
import anda.travel.driver.baselibrary.utils.TypeUtil;

/**
 * 功能描述：
 */
public class OrderSummaryVO {

    public static OrderSummaryVO createFrom(OrderSummaryEntity entity) {
        if (entity == null) return new OrderSummaryVO();
        String strJson = JSON.toJSONString(entity);
        return JSON.parseObject(strJson, OrderSummaryVO.class);
    }

    public String uuid; // "e7075e98961c42228b0fbf9e08ed75a2",
    public String originAddress; // "福建省厦门市思明区观日路靠近观日路24之一号楼",
    public String destAddress; // "仙岳路4666号",
    public Integer typeTime; // 1,
    public Integer typeTrip; // 1,
    public Float totalFare; // 10,
    public Long deparTime; // 1483497499000,
    public Integer actualPasNum; // 1,
    public Integer subStatus; // 602

    // 2017-02-27 追加字段
    /**
     * 订单主状态
     */
    public Integer mainStatus;

    // 2017-04-19 追加字段
    public Double drvTotalFare; //扣除"抽成"后的收入

    // 2017-04-27 追加字段
    public Integer payStatus; //支付状态

    public int riskStatus;//是否是风险订单(1:是，2:否)

    public int canHurryPay;//是否可催款(0: 不可催款 1: 可催款)

    public String getDestAddress() {
        return TextUtils.isEmpty(destAddress) ? "" : destAddress;
    }

    public String getOriginAddress() {
        return TextUtils.isEmpty(originAddress) ? "" : originAddress;
    }

    public String getStrFare() {
        if (drvTotalFare == null) return "";
        return NumberUtil.getFormatValue(drvTotalFare) + "元";
    }

    public long getDepartTime() {
        return TypeUtil.getValue(deparTime);
    }

}
