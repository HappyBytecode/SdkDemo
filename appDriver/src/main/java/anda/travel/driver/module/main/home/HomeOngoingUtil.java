package anda.travel.driver.module.main.home;

/**
 * @Author moyuwan
 * @Date 18/3/26
 */
public class HomeOngoingUtil {

    // 首页是否有弹出"进行中订单"
    private static boolean IS_ORDER_ONGOING = false;

    public static void setIsOrderOngoing(boolean isOrderOngoing) {
        IS_ORDER_ONGOING = isOrderOngoing;
    }

    public static boolean isOrderOngoing() {
        return IS_ORDER_ONGOING;
    }

}
