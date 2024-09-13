package anda.travel.driver.event;

public class GaoDeServiceEvent extends BaseEvent {

    public final static int isNotInService = 0;
    public final static int isInService = 1;

    public GaoDeServiceEvent(int type) {
        super(type);
    }

    public GaoDeServiceEvent(int type, Object obj1) {
        super(type, obj1);
    }

    public GaoDeServiceEvent(int type, Object obj1, Object obj2) {
        super(type, obj1, obj2);
    }

}
