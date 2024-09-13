package anda.travel.driver.data.entity;

import java.io.Serializable;

public class AliAccountEntity implements Serializable {

    private String payeeAccount; // 收款人账户
    private String payeeName;    // 收款人姓名

    public String getPayeeAccount() {
        return payeeAccount;
    }

    public void setPayeeAccount(String payeeAccount) {
        this.payeeAccount = payeeAccount;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }
}
