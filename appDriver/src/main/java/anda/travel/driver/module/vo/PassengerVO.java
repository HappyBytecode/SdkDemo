package anda.travel.driver.module.vo;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * 功能描述：乘客信息
 */
public class PassengerVO implements Serializable {

    public String uuid; // "368aef4418064fcbb65a3d866a36f59c",
    public String mobile; // 手机号码
    public String nickname; // 昵称
    public Integer sex; // 性别(1男、2女)
    public String token; // token
    public Integer point; // 积分
    public Double balance; // 余额
    public Integer orderCount; // 乘客成功订单总数
    public Integer status; // 状态（1.正常，2.长期封号，3.短期封号）
    public String abortRemark; // 封号备注
    public Integer isFirst; // 是否首次登录 1：是、2：否
    public String name; ///乘客后四位尾号
    public String phoneSuffix; ///乘客真实手机号码后四位尾号

    @JSONField(name = "avatar")
    public String face; // 头像

    public String getPassengerRealPhoneEnd() {
        return phoneSuffix;
    }

}
