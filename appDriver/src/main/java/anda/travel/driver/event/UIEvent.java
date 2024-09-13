package anda.travel.driver.event;

public class UIEvent extends BaseEvent {

    public final static int CLEAR_PWD = 1; //重置密码
    public final static int CLOSE_PWD_ACTIVITY = 2; //关闭密码输入页
    public final static int ONE_KEY_LOGIN = 4; //显示一键登录按钮
    public final static int CLOSE_BIND_ALI_ACCOUNT_ACTIVITY = 5;//关闭账号绑定页面

    public UIEvent(int type) {
        super(type);
    }

    public UIEvent(int type, Object obj1) {
        super(type, obj1);
    }

    public UIEvent(int type, Object obj1, Object obj2) {
        super(type, obj1, obj2);
    }

}