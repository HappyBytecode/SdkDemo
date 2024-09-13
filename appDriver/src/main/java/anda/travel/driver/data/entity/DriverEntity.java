package anda.travel.driver.data.entity;

/**
 * 功能描述：司机信息
 */
public class DriverEntity {

    public Integer isFirst; //是否首次登陆 1.是 2.否

    public String uuid; // 用户ID
    public String adcode; // 350200,
    public String mobile; // 手机号码
    public String type; // 司机类型
    public String name; // 姓名
    public String avatar; // 头像
    public String sex; // 性别 1.男 2.女
    public String countConfirm; // 0,
    public String countComplete; // 0,
    public String score; // 平均分数
    public String abortRemark; // null,
    public String balance; // 账号余额
    public String appointTimeStart; // null,
    public String appointTimeEnd; // null,
    public String remindType; // 1,
    public String vehicleUuid; // "VEHICLE1000000000000000000010001",
    public String vehicleNo; // "闽DN1001",
    public String vehicleBrand; // "宝鼠牌",
    public String vehicleModel; // null,
    public String vehLvUuid; // "VEHLV100000000000000000000010001",
    public String vehicleType; // 1,
    public String vehicleTypeName; // "经济型",
    public String vehicleColor; //车辆颜色
    public String fareUuid; // "SYSFARE1000000000000000000010001",
    public String token; // "e397a627ecae493789b4a670bc5f1cde"

    public String face;
    //公司名
    public String shortName;
    public String phone;
    //公司手机
    public String responsibleMobile;
    //乘客成功订单总数
    public Integer orderCount;
    //状态 1.正常 2.长期封号 3.短期封号
    public Integer status;
    //车牌号
    public String plateNum;
    //车外观颜色
    public String carColor;
    //车品牌名称
    public String brandName;
    //经度
    public Double lng;
    //纬度
    public Double lat;
    //当前角度
    public String currentAngle;
    //审核中提现金额
    public Double onCashAmount;

    public String driverHxName;
    public String driverHxPwd;
    public String contactHxName;

    //2017-04-26 追加字段
    public Integer depend; //是否是"自营司机" 1.自营、2.加盟

    //2018-03-07 追加字段
    public Integer vehDepend; //车辆归属字段  1.自由、2.挂靠

    //2017-05-22 追加字段
    public Integer mileType; //采用的里程计算方案 1.轨迹纠偏、2.两点距离累加
    public String actualName; //真实姓名
    public Double canCashAmount; //可提现金额（可提现余额 = 余额 - 审核中金额 - 本周一到当前的收入）

    //2017-06-01 追加字段
    public String txIdentifier; //腾讯云司机标示
    public String txUserSig; //腾讯云司机签名
    public String txSupport; //腾讯云客服标示

    //2017-07-20 追加字段
    public Double mileage; //已行驶的里程

    //2017-08-24 追加字段
    public String bankcard; //最近一次提现成功的银行卡号

    //2017-01-24 追加字段
    public String labelName; //所属公司简称

    //2018-03-02 追加字段
    public Integer substitute; //是否为代班司机

    //2018-10-08
    public String bankName; //开户银行
    public String bankCity; //开户城市
    public String bankProvince; //开户城市

    //2022-07-27
    public Integer pushType;//获取司机订单推送模式

    // 支付宝提现
    public String aliNickName;
    public String aliAccount;
    public String aliUserId;

    public String identify; //登陆司机身份标识(1:运营司机，2:注册司机)

}
