package anda.travel.driver.config;

/**
 * 功能描述：验证码类型
 */
public enum CodeType {

    LOGIN(1),
    MODIFY(2),
    BIND_ALI_ACCOUNT(12),
    WITHDRAWAL(13),
    CHANGE_MOBILE(14);

    private final int type;

    CodeType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

}
