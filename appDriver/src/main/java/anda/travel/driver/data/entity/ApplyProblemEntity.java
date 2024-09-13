package anda.travel.driver.data.entity;

import java.io.Serializable;

public class ApplyProblemEntity implements Serializable {
    public String uuid;
    public String title;
    public String status;//状态1显示 2隐藏
    public String linkUrl;//链接地址
    public String identity;//类别（1：乘客、2：司机）
}
