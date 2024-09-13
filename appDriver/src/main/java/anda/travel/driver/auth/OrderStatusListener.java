package anda.travel.driver.auth;

/**
 * @author Lenovo
 */
public interface OrderStatusListener {
    /**
     * 订单状态获取
     *
     * @param isOrdering 是否有进行中订单
     */
    void orderStatusObtain(boolean isOrdering, String orderUuid);
}
