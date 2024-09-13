package anda.travel.driver.event;

public class SlideViewEvent extends BaseEvent {

    public SlideViewEvent(int type) {
        super(type);
    }

    public SlideViewEvent(int type, Object obj1) {
        super(type, obj1);
    }

    public SlideViewEvent(int type, Object obj1, Object obj2) {
        super(type, obj1, obj2);
    }

    public SlideViewEvent() {
        super(1);
    }
}