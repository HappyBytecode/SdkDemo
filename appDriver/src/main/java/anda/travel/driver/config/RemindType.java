package anda.travel.driver.config;

/**
 * 功能描述：
 */
public enum RemindType {

    ALL(1), //全部
    REALTIME(2), //实时
    APPOINT(3); //预约

    private final int type;

    RemindType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

}
