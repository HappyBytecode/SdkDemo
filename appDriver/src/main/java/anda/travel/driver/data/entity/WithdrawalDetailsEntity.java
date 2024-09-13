package anda.travel.driver.data.entity;

/**
 * 提现详情
 */
public class WithdrawalDetailsEntity {
    /**
     * ID
     **/
    public String uuid;

    /**
     * 用户ID
     **/
    public String userUuid;

    /**
     * 提现金额
     **/
    public Double cash;

    /**
     * 状态 1：提交申请，2：提现成功，3：提现失败
     **/
    public Integer status;

    /**
     * 提现失败理由
     **/
    public String reason;

    /**
     * 执行时间
     **/
    public java.util.Date handlingOn;

    /**
     * 执行者
     **/
    public Integer handlingBy;

    /**
     * 付款类型 1：企业账户
     **/
    public String payType;

    /**
     * 付款账号
     **/
    public String payAccount;

    /**
     * 付款人
     **/
    public String payName;

    /**
     * 收款账号
     **/
    public String collectAccount;

    /**
     * 收款人
     **/
    public String collectName;

    /**
     * 收款类型 1：银联卡，2：ic_share_wechat 3.支付宝
     **/
    public int collectType;

    /**
     * 由支付平台返回
     **/
    public String serialNum;

    /**
     * 创建时间（申请时间）
     **/
    public Long createTime;

    /**
     * 更新时间
     **/
    public Long processTime;

    /**
     * 更新者
     **/
    public String updater;

    /**
     * 手续费
     */
    public Double cashFee;
}
