package anda.travel.driver.data.entity;

import java.io.Serializable;

public class ApplyInfoEntity implements Serializable {
    public static final int WAITING = 1;
    public static final int PASS = 2;
    public static final int FAILED = 3;
    public static final int NO_APPLY = 4;
    public String mobile;
    public String operateUuid;
    public String remark;
    public int status;//状态（1.待审核,2审核通过，3审核失败，4，未提交/待提交）
}
