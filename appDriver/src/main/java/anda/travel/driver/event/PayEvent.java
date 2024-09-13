package anda.travel.driver.event;

/**
 * 功能描述：
 */
public class PayEvent extends BaseEvent {

    public final static int PAY_SUCCESS = 1; //支付成功
    public final static int PAY_FAIL = 2; //支付失败

    public PayEvent(int type) {
        super(type);
    }

    public PayEvent(int type, Object obj1) {
        super(type, obj1);
    }

    public PayEvent(int type, Object obj1, Object obj2) {
        super(type, obj1, obj2);
    }

}
