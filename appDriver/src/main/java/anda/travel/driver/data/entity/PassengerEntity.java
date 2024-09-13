package anda.travel.driver.data.entity;

/**
 * 功能描述：乘客信息（部分字段名称和服务端实际返回的不同）
 */
public class PassengerEntity {

    public String uuid; // "368aef4418064fcbb65a3d866a36f59c",
    public String mobile; // 手机号码
    public String nickname; // 昵称
    public Integer sex; // 性别(1男、2女)
    public String avatar; // 头像
    public String token; // token
    public Integer point; // 积分
    public Double balance; // 余额
    public Integer orderCount; // 乘客成功订单总数
    public Integer status; // 状态（1.正常，2.长期封号，3.短期封号）
    public String abortRemark; // 封号备注
    public Integer isFirst; // 是否首次登录 1：是、2：否
    public String name;
    public String phoneSuffix;
}
