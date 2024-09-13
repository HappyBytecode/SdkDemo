package anda.travel.driver.baselibrary.utils;

////导航需要的工具类
public class NaviUtil {
    public static String formatKM(int d) {
        if (d < 1000) {
            return d + "";
        } else if ((1000 <= d) && (d < 10000)) {
            return (d / 10) * 10 / 1000.0D + "";
        } else if ((10000 <= d) && (d < 100000)) {
            return (d / 100) * 100 / 1000.0D + "";
        }
        return (d / 1000) + "";
    }

    public static String getUnit(int d) {
        if (d < 1000) {
            return "米";
        } else {
            return "公里";
        }
    }

    public static int getMinute(int secTime) {
        int minute = secTime / 60;
        if (secTime % 60 >= 30) minute += 1; //秒数多于30，分钟加1
        if (minute == 0) minute = 1; ////如果为0显示1分钟
        return minute;
    }
}
