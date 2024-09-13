package anda.travel.driver.data.entity;

/**
 * 功能描述：
 */
public class ComplainResultEntity {

    public String uuid; // "cc5b899e8d4443fd986e6ca546d5ae9b",

    /**
     * 1：待处理，2：正在处理，3：已处理
     */
    public Integer status;

    public String contents; //投诉内容

    public String remark; //额外的投诉备注

    public String result; //投诉结果

}
