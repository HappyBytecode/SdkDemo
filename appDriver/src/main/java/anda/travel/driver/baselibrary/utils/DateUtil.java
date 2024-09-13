package anda.travel.driver.baselibrary.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期工具类
 */
public final class DateUtil {

    /**
     * 日期类型
     */
    public static final String yyyyMMDD = "yyyyMMdd";
    public static final String yyyyMMDD_SPLIT = "yyyy-MM-dd";
    public static final String yyyyMM_SPLIT = "yyyy-MM";
    public static final String yyyyMM = "yyyyMM";
    public static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
    public static final String HHmmss = "HH:mm:ss";
    public static final String hhmmss = "HH:mm:ss";
    public static final String DB_DATA_FORMAT = "yyyy-MM-DD HH:mm:ss";
    public static final String NEWS_ITEM_DATE_FORMAT = "hh:mm M月d日 yyyy";
    public static final String MMddHHmmss = "MM-dd HH:mm";
    /**
     * yyyy年M月d日 HH:mm:ss
     */
    public static final String LOCALE_DATE_FORMAT_ACCURATE_TO_SECOND = "yyyy年M月d日 HH:mm:ss";
    /**
     * yyyy年M月d日 HH:mm
     */
    public static final String LOCALE_DATE_FORMAT_ACCURATE_TO_MINUTES = "yyyy年M月d日 HH:mm";
    /**
     * yyyy年M月d日
     */
    public static final String LOCALE_DATE_FORMAT_ACCURATE_TO_DAY = "yyyy年M月d日";
    /**
     * yy年M月d日
     */
    public static final String LOCALE_DATE_FORMAT_ACCURATE_TO_DAY2 = "yy年M月d日";
    /**
     * M月d日
     */
    public static final String LOCALE_DATE_FORMAT_ACCURATE_TO_DAY3 = "M月d日";

    public static String dateToString(Date date, String pattern)
            throws Exception {
        return new SimpleDateFormat(pattern).format(date);
    }

    public static Date stringToDate(String dateStr, String pattern)
            throws Exception {
        return new SimpleDateFormat(pattern).parse(dateStr);
    }

    public static String timeStampToString(long stamp, String timeType) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(timeType);
        return dateFormat.format(new Date(stamp));
    }

    /**
     * 将Date类型转换为日期字符串
     *
     * @param date Date对象
     * @param type 需要的日期格式
     * @return 按照需求格式的日期字符串
     */
    public static String formatDate(Date date, String type) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(type);
            return df.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将日期字符串转换为Date类型
     *
     * @param dateStr 日期字符串
     * @param type    日期字符串格式
     * @return Date对象
     */
    public static Date parseDate(String dateStr, String type) {
        SimpleDateFormat df = new SimpleDateFormat(type);
        Date date = null;
        try {
            date = df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 得到年
     *
     * @param date Date对象
     * @return 年
     */
    public static int getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.YEAR);
    }

    /**
     * 得到月
     *
     * @param date Date对象
     * @return 月
     */
    public static int getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MONTH) + 1;
    }

    /**
     * 得到日
     *
     * @param date Date对象
     * @return 日
     */
    public static int getDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 转换日期 将日期转为今天, 昨天, 前天, XXXX-XX-XX, ...
     *
     * @param time 时间
     * @return 当前日期转换为更容易理解的方式
     */
    public static String translateDate(Long time) {
        long oneDay = 24 * 60 * 60 * 1000;
        Calendar current = Calendar.getInstance();
        Calendar today = Calendar.getInstance();    //今天

        today.set(Calendar.YEAR, current.get(Calendar.YEAR));
        today.set(Calendar.MONTH, current.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
        //  Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        long todayStartTime = today.getTimeInMillis();

        if (time >= todayStartTime && time < todayStartTime + oneDay) { // today
            return "今天";
        } else if (time >= todayStartTime - oneDay && time < todayStartTime) { // yesterday
            return "昨天";
        } else if (time >= todayStartTime - oneDay * 2 && time < todayStartTime - oneDay) { // the day before yesterday
            return "前天";
        } else if (time > todayStartTime + oneDay) { // future
            return "将来某一天";
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date(time);
            return dateFormat.format(date);
        }
    }

    /**
     * 转换日期 转换为更为人性化的时间
     *
     * @param time 时间
     * @return
     */
    public static String translateDate(long time, long curTime) {
        long oneDay = 24 * 60 * 60;
        Calendar today = Calendar.getInstance();    //今天
        today.setTimeInMillis(curTime);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        long todayStartTime = today.getTimeInMillis();
        if (time >= todayStartTime) {
            long d = curTime - time;
//            if (d <= 60) {
//                return "1分钟前";
//            } else if (d <= 60 * 60) {
//                long m = d / 60;
//                if (m <= 0) {
//                    m = 1;
//                }
//                return m + "分钟前";
//            } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("今天HH:mm");
            Date date = new Date(time);
            String dateStr = dateFormat.format(date);
            if (!TextUtils.isEmpty(dateStr) && dateStr.contains(" 0")) {
                dateStr = dateStr.replace(" 0", " ");
            }
            return dateStr;
//            }
        } else {
            if (time > todayStartTime - oneDay) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("昨天HH:mm");
                Date date = new Date(time);
                String dateStr = dateFormat.format(date);
                if (!TextUtils.isEmpty(dateStr) && dateStr.contains(" 0")) {

                    dateStr = dateStr.replace(" 0", " ");
                }
                return dateStr;
            } else if (time < todayStartTime - oneDay && time > todayStartTime - 2 * oneDay) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("前天HH:mm");
                Date date = new Date(time);
                String dateStr = dateFormat.format(date);
                if (!TextUtils.isEmpty(dateStr) && dateStr.contains(" 0")) {
                    dateStr = dateStr.replace(" 0", " ");
                }
                return dateStr;
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date = new Date(time);
                String dateStr = dateFormat.format(date);
                if (!TextUtils.isEmpty(dateStr) && dateStr.contains(" 0")) {
                    dateStr = dateStr.replace(" 0", " ");
                }
                return dateStr;
            }
        }
    }

    /**
     * 字符类型时间转long
     *
     * @param time
     * @return
     */
    public static long parseLong(String time) {
        long lSysTime1 = 0;
        try {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sf.parse(time);
            lSysTime1 = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return lSysTime1;
    }

    /**
     * @param time 单位：ms
     * @return
     */
    public static String getTodayOrTomorrow(long time) {
        Date date = new Date(time);
        //date 是存储的时间戳
        //所在时区时8，系统初始时间是1970-01-01 80:00:00，注意是从八点开始，计算的时候要加回去
        int offSet = Calendar.getInstance().getTimeZone().getRawOffset();
        long today = (System.currentTimeMillis() + offSet) / 86400000;
        long start = (time + offSet) / 86400000;
        long intervalTime = start - today;
        //0：今天,1：明天,2：后天
        String strDes = "";
        if (intervalTime == 0) {
            strDes = "今天";//今天
            strDes += formatDate(date, "HH:mm");
        } else if (intervalTime == 1) {
            strDes = "明天";//明天
            strDes += formatDate(date, "HH:mm");
        } else if (intervalTime == 2) {
            strDes = "后天";//后天
            strDes += formatDate(date, "HH:mm");
        } else {
            strDes = formatDate(date, "MM月dd日HH:mm");
        }
        return strDes;
    }

    /**
     * @param time 单位：ms
     * @return
     */
    public static String getSpace(long time) {
        Date date = new Date(time);
        String strDes = "";
        strDes += formatDate(date, "HH:mm");
        return strDes;
    }

    /**
     * @param time 单位：ms
     * @return
     */
    public static String getRange(long time) {
        Date date = new Date(time);
        //date 是存储的时间戳
        //所在时区时8，系统初始时间是1970-01-01 80:00:00，注意是从八点开始，计算的时候要加回去
        int offSet = Calendar.getInstance().getTimeZone().getRawOffset();
        long today = (System.currentTimeMillis() + offSet) / 86400000;
        long start = (time + offSet) / 86400000;
        long intervalTime = start - today;
        //0：今天,1：明天,2：后天
        String strDes = "";
        if (intervalTime == 0) {
            strDes = "今天";//今天
        } else if (intervalTime == 1) {
            strDes = "明天";//明天
        } else if (intervalTime == 2) {
            strDes = "后天";//后天
        } else {
            strDes = formatDate(date, "MM月dd日");
        }
        return strDes;
    }

    /**
     * 把毫秒转化成日期
     *
     * @param dateFormat (日期格式，例如：MM/dd/yyyy HH:mm:ss)
     * @param millSec    (毫秒数)
     * @return
     */
    public static String transferLongToDate(String dateFormat, long millSec) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date(millSec);
        return sdf.format(date);
    }

    /**
     * 星期几
     *
     * @param date Date 日期
     * @return 星期一到星期日
     */
    public static String getWeekOfDate(Date date) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

        return weekDays[w];
    }

    /**
     * 星期几
     *
     * @param time long 系统时间的long类型
     * @return 星期一到星期日
     */
    public static String getWeekOfDate(long time) {
        Date date = new Date(time);
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

        return weekDays[w];
    }

    /**
     * 返回年-月-日 hh:ff:ss 星期几
     *
     * @param time long系统时间
     * @return String 例如2013-05-26 19:39:26 星期日
     */

    public static String getDateAndWeek(long time) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = new Date(time);

        String dateString = format.format(date);

        String dayString = getWeekOfDate(date);

        return dateString + " " + dayString;

    }

    public static String stringToString(String stamp, String timeType) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(timeType);
        try {
            Long timeStamp = Long.valueOf(stamp);
            return dateFormat.format(new Date(timeStamp));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     *
     * @param nowTime   当前时间
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        return date.after(begin) && date.before(end);
    }

    /**
     * 判断两个日期的大小 2016-4-12
     */
    public static boolean compareTime(String first, String second) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        try {
            Date firstdate = sdf.parse(first);
            Date seconddate = sdf.parse(second);
            boolean flag = !firstdate.after(seconddate);
            if (flag) {
                ////早于第二个日期 <
                return true;
            } else {
                ///晚于第二个日期 >
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
