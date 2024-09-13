package anda.travel.driver.module.vo;

import java.util.Calendar;

/**
 * 功能描述：时间
 */
public class TimeVO {

    public static TimeVO createFrom(Long timeStamp) {
        if (timeStamp == null) return null;

        TimeVO vo = new TimeVO();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeStamp);
        vo.year = cal.get(Calendar.YEAR);
        vo.month = cal.get(Calendar.MONTH) + 1;
        vo.day = cal.get(Calendar.DAY_OF_MONTH);
        vo.hour = cal.get(Calendar.HOUR_OF_DAY);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        vo.timeStmap = cal.getTimeInMillis();
        return vo;
    }

    public int year; //年
    public int month; //月
    public int day; //日
    public int hour; //时
    public long timeStmap; //对应的时间戳

    public String getStrTime() {
        StringBuilder str = new StringBuilder();
        if (day < 10) str.append(0);
        str.append(day);
        str.append("日");
        if (hour < 10) str.append(0);
        str.append(hour);
        str.append("时");
        return str.toString();
    }
}
