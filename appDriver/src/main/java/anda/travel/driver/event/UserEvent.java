package anda.travel.driver.event;

public class UserEvent extends BaseEvent {

    public static final int LOGIN = 1;
    public static final int LOGOUT = 2;
    public static final int RESET_PWD_SUCCESS = 3; //修改密码
    public static final int CHECK_AND_LOGIN = 4; //验证并登录

    public UserEvent(int type) {
        super(type);
    }

    public UserEvent(int type, Object obj1) {
        super(type, obj1);
    }

    public UserEvent(int type, Object obj1, Object obj2) {
        super(type, obj1, obj2);
    }

}