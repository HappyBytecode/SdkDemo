package anda.travel.driver.data.entity;

/**
 * 功能描述：订单概要
 */
public class OrderSummaryEntity {

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

    public int carpool;//1.拼车单 2.非拼车单

    public int riskStatus;//是否是风险订单(1:是，2:否)

    public String hintPaymentSwitch;

    public int canHurryPay;//是否可催款(0: 不可催款 1: 可催款)
}
