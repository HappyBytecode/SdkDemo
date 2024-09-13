package anda.travel.driver.event;

/**
 * 功能描述：
 */
public class DutyEvent extends BaseEvent {

    public static final int ON_DUTY = 1; //出车
    public static final int OFF_DUTY = 2; //收车
    public static final int SYS_OFF_DUTY = 3; //收车
    public static final int FORCE_OFF_DUTY = 11; //被强制收车
    public static final int REFRESH_DUTY = 12; //刷新状态
    public static final int FOREGROUND = 21; //应用在前台
    public static final int BACKGROUND = 22; //应用在后台
    public static final int FEMALE_FORBIDDEN_NIGHT = 4; ////禁止女司机夜间出车

    public DutyEvent(int type) {
        super(type);
    }

    public DutyEvent(int type, Object obj1) {
        super(type, obj1);
    }

    public DutyEvent(int type, Object obj1, Object obj2) {
        super(type, obj1, obj2);
    }

}
