package anda.travel.driver.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimeUtils {

    /**
     * 解析日期（格式 yyyy-MM）
     *
     * @param date
     * @return
     */
    public static String parseDate(long date) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM");
        return sf.format(new Date(date));
    }

    /**
     * 解析时间(格式 MM-dd HH:mm)
     *
     * @param time
     * @return
     */
    public static String paseTime(long time) {
        SimpleDateFormat sf = new SimpleDateFormat("MM-dd HH:mm");
        return sf.format(new Date(time));
    }

    /**
     * 解析时间(格式 yyyy-MM-dd HH:mm)
     *
     * @param time
     * @return
     */
    public static String paseDateAndTime(long time) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sf.format(new Date(time));
    }

    /**
     * 字符类型时间转long
     *
     * @param time
     * @return
     */
    public static long paseLong(String time) {
        long lSysTime1 = 0;
        try {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = sf.parse(time);
            lSysTime1 = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return lSysTime1;
    }

    public static long paseLongWithYear(String time) {
        long lSysTime1 = 0;
        try {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = sf.parse(time);
            lSysTime1 = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return lSysTime1;
    }

    /**
     * 获得时长：天
     *
     * @param time
     * @return
     */
    private static long getDay(long time) {
        return time / (1000 * 60 * 60 * 24);
    }

    /**
     * 获得时长：小时
     *
     * @param time
     * @return
     */
    public static long getHour(long time) {
        return (time - getDay(time) * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
    }

    /**
     * 获得时长： 只获取小时
     *
     * @param time
     * @return
     */
    public static long getHourOnly(long time) {
        return time / (1000 * 60 * 60);
    }

    /**
     * 获得时长：小时
     *
     * @param time
     * @return
     */
    public static String getHourWithDecimal(long time) {
        double value = time / (1000 * 60 * 60 * 1.00d);
        return new DecimalFormat("0.00").format(value);
    }

    /**
     * 获得时长：分
     *
     * @param time
     * @return
     */
    public static long getMinute(long time) {
        return (time - getDay(time) * (1000 * 60 * 60 * 24) - getHour(time) * (1000 * 60 * 60)) / (1000 * 60);
    }

    /**
     * 获得当前系统时间
     *
     * @param patterm 格式
     * @return
     */
    private static String getTime(String patterm) {
        SimpleDateFormat sf = new SimpleDateFormat(patterm);
        return sf.format(new Date());
    }

    /**
     * 获得从当天算起的15天的日期（格式：MM-dd）
     *
     * @return
     */
    public static String[] getDates() {
        String[] date = new String[15];
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        date[0] = sf.format(c.getTime());
        for (int i = 1; i < 15; i++) {
            c.add(Calendar.DAY_OF_YEAR, 1);
            date[i] = sf.format(c.getTime());
        }
        return date;
    }

    /**
     * 获得从明天算起的14天的日期（格式：MM-dd）
     *
     * @return
     */
    public static String[] getDates2() {
        String[] date = new String[15];
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        c.add(Calendar.DAY_OF_YEAR, 1);
        date[0] = sf.format(c.getTime());
        for (int i = 1; i < 15; i++) {
            c.add(Calendar.DAY_OF_YEAR, 1);
            date[i] = sf.format(c.getTime());
        }
        return date;
    }

    /**
     * 获得今天的日期(格式：MM-dd)
     *
     * @return
     */
    public static String getToady() {
        SimpleDateFormat sf = new SimpleDateFormat("MM-dd");
        return sf.format(new Date());
    }

    public static String getTodayWithYear() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(new Date());
    }

    public static String getTodayWithYearChina() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日");
        return sf.format(new Date());
    }

    public static String getTodayWithYearChinaButDay() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月");
        return sf.format(new Date());
    }

    public static String getTodayWithYearNoSplit() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        return sf.format(new Date());
    }

    public static String getTodayWithYearSplit() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(new Date());
    }

    public static String getTodayWithYearSplitButDay() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM");
        return sf.format(new Date());
    }

    /**
     * 获得明天的日期(格式：MM-dd)
     *
     * @return
     */
    public static String getTomorrow() {
        SimpleDateFormat sf = new SimpleDateFormat("MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DAY_OF_YEAR, 1);
        return sf.format(c.getTime());
    }

    public static String getTomorrowWithYear() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DAY_OF_YEAR, 1);
        return sf.format(c.getTime());
    }

    /**
     * 获得后天的日期(格式：MM-dd)
     *
     * @return
     */
    public static String getBermorgen() {
        SimpleDateFormat sf = new SimpleDateFormat("MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DAY_OF_YEAR, 2);
        return sf.format(c.getTime());
    }

    public static String getBermorgenWithYear() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DAY_OF_YEAR, 2);
        return sf.format(c.getTime());
    }

    /**
     * 获得所有时间段
     *
     * @return
     */
    public static String[] getAllTimes() {
        String[] time = {"00:00", "00:30",
                "01:00", "01:30",
                "02:00", "02:30",
                "03:00", "03:30",
                "04:00", "04:30",
                "05:00", "05:30",
                "06:00", "06:30",
                "07:00", "07:30",
                "08:00", "08:30",
                "09:00", "09:30",
                "10:00", "10:30",
                "11:00", "11:30",
                "12:00", "12:30",
                "13:00", "13:30",
                "14:00", "14:30",
                "15:00", "15:30",
                "16:00", "16:30",
                "17:00", "17:30",
                "18:00", "18:30",
                "19:00", "19:30",
                "20:00", "20:30",
                "21:00", "21:30",
                "22:00", "22:30",
                "23:00", "23:30"};
        return time;
    }

    public static String[] getAllTimes2() {
        String[] time = {"00:30",
                "01:00", "01:30",
                "02:00", "02:30",
                "03:00", "03:30",
                "04:00", "04:30",
                "05:00", "05:30",
                "06:00", "06:30",
                "07:00", "07:30",
                "08:00", "08:30",
                "09:00", "09:30",
                "10:00", "10:30",
                "11:00", "11:30",
                "12:00", "12:30",
                "13:00", "13:30",
                "14:00", "14:30",
                "15:00", "15:30",
                "16:00", "16:30",
                "17:00", "17:30",
                "18:00", "18:30",
                "19:00", "19:30",
                "20:00", "20:30",
                "21:00", "21:30",
                "22:00", "22:30",
                "23:00", "23:30"};
        return time;
    }

    /**
     * 获得最近时间点的位置
     *
     * @return
     */
    private static int getTargetTimeIndex() {
        int index = 0;
        String curTime = TimeUtils.getTime("HH:mm");
        String[] times = getAllTimes();
        for (int i = 0; i < times.length; i++) {
            if (i > 0 && i + 1 < times.length && curTime.compareTo(times[i - 1]) > 0 && curTime.compareTo(times[i]) <= 0) {
                index = i + 1;
                break;
            }
        }
        return index;
    }

    /**
     * 获得当前的时间点
     *
     * @return
     */
    public static String getCurTime() {
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
        return sf.format(new Date());
    }

    /**
     * 获得最近为开始的时间段
     *
     * @return
     */
    public static String[] getTimes() {
        int index = getTargetTimeIndex();
        List<String> list = new ArrayList<>();
        for (int i = index; i < getAllTimes().length; i++) {
            list.add(getAllTimes()[i]);
        }
        return list.toArray(new String[getAllTimes().length - 1 - index]);
    }

    /**
     * 获得当前时间（格式:hh:mm）
     *
     * @return
     */
    public static String getTime() {
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
        return sf.format(new Date());
    }

    /**
     * 判断是否是星期三
     *
     * @return
     */
    public static boolean isWednesday() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        return c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY;
    }

    public static String getTime(long duration) {
        if (duration < 1000) return "00:00";
        StringBuilder str = new StringBuilder();
        int second = (int) (duration / 1000);
        int min = second / 60; //分钟
        int sec = second % 60; //秒数
        if (min < 10) str.append("0");
        str.append(min);
        str.append(":");
        if (sec < 10) str.append("0");
        str.append(sec);
        return str.toString();
    }

    public static String formatTime(long time, String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(new Date(time));
    }

    /**
     * Date转日期字符串
     *
     * @return
     */
    public static String date2Str(Date date) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日");
        return sf.format(date);
    }

    public static String date2StrButDay(Date date) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月");
        return sf.format(date);
    }

}
