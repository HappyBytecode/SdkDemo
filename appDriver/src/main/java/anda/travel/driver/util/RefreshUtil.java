package anda.travel.driver.util;

/**
 * @Author moyuwan
 * @Date 18/3/13
 */
public class RefreshUtil {

    //是否刷新
    private static boolean REFRESH = false;

    public static void setRefresh(boolean refresh) {
        REFRESH = refresh;
    }

    public static boolean isRefresh() {
        return REFRESH;
    }

}
