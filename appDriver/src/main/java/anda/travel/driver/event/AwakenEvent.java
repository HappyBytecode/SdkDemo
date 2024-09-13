package anda.travel.driver.event;

public class AwakenEvent extends BaseEvent {

    public static final int AWAKEN_EVENT = 1;

    public AwakenEvent(int type) {
        super(type);
    }

    public AwakenEvent(int type, Object obj1) {
        super(type, obj1);
    }

    public AwakenEvent(int type, Object obj1, Object obj2) {
        super(type, obj1, obj2);
    }

    public AwakenEvent(int type, Object obj1, Object obj2, Object obj3) {
        super(type, obj1, obj2, obj3);
    }
}
