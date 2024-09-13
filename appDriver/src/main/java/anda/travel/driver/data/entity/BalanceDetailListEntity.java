package anda.travel.driver.data.entity;

import java.util.List;

/**
 * 功能描述：（司机余额明细列表）
 */
public class BalanceDetailListEntity {

    public String detailDate;
    public double total;
    public List<BalanceDetailEntity> items;
    public boolean isTotal;
    public String name;
    public long operateTime;
    public double amount;
    public String remark;

    public static class BalanceDetailEntity {
        public String name;
        public long operateTime;
        public double amount;
        public String remark;
    }

}
