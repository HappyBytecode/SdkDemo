package anda.travel.driver.baselibrary.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class NumberUtil {

    /**
     * 获取格式化后的数据
     *
     * @param value
     * @return
     */
    public static String getFormatValue(Double value) {
        return getFormatValue(value, true);
    }

    public static String getFormatValue(Double value, boolean isPrice) {
        if (value == null || value == 0) return isPrice ? "0.00" : "0";

        return isPrice
                ? new DecimalFormat("0.00").format(value)
                : new DecimalFormat("#.##").format(value);
    }

    /**
     * 获取格式化后的数据
     *
     * @param value
     * @return
     */
    public static String getFormatValue(Float value) {
        if (value == null || value == 0) return "0";
        return new DecimalFormat("#.##").format(value);
    }

    /**
     * 获取格式化后的数据
     *
     * @param strValue
     * @return
     */
    public static String getFormatValue(String strValue, boolean isPrice) {
        try {
            if (!TextUtils.isEmpty(strValue)) {
                return getFormatValue(Double.valueOf(strValue), isPrice);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isPrice ? "0.00" : "0";
    }

    public static double change2Double(String value) {
        try {
            if (!TextUtils.isEmpty(value)) {
                return Double.parseDouble(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static double change2Double(Double value) {
        if (value == null || value == 0) return 0;
        return value;
    }

    public static String getFormatValueForMile(String strValue, boolean isPrice) {
        try {
            BigDecimal bigDecimal = new BigDecimal(strValue);
            // 转换为万元（除以10000）
            BigDecimal decimal = bigDecimal.divide(new BigDecimal("10000"));
            // 保留两位小数
            DecimalFormat formater;
            if (isPrice) {
                formater = new DecimalFormat("0.00");
            } else {
                formater = new DecimalFormat("0.0");
            }

            // 四舍五入
            formater.setRoundingMode(RoundingMode.HALF_UP);    // 5000008.89

            // 格式化完成之后得出结果
            String formatNum = formater.format(decimal);
            return formatNum;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0.00";
    }

    //////////只截取位数后的数据，使用ROUND_HALF_DOWN不四舍五入
    public static double round(Double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b = null == v ? new BigDecimal("0.0") : new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 小数转换成百分比
     *
     * @param str
     * @return
     */
    public static String convertPercent(double str) {
        DecimalFormat df = new DecimalFormat("0.0%");
        return df.format(str);
    }
}
