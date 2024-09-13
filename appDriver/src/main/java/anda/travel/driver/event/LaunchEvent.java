package anda.travel.driver.event;

public class LaunchEvent extends BaseEvent {
    /**
     * 跳转首页
     */
    public static final int JUMP_HOME = 116;

    public LaunchEvent(int type) {
        super(type);
    }

}
