package anda.travel.driver.util;

import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 功能描述：EditText输入的限制
 */
public class FilterUtils {

    // 限定：只能输入中文
    public static String nameFilter(String str) throws PatternSyntaxException {
        String regEx = "[^[\u4e00-\u9fa5],{0,}$]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    // 限定：前17为为数字，第18位为数字或字母X
    public static String idFilter(String str) throws PatternSyntaxException {
        String regEx = null;
        if (str.length() == 18) {
            regEx = "\\d{17}[^0-9xX]";
        } else {
            regEx = "[^0-9]";
        }
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    // 限定：只允许字母和数字
    public static String carNoFilter(String str) throws PatternSyntaxException {
        String regEx = "[^a-zA-Z0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 检查url的合法性
     *
     * @param url
     * @return
     */
    public static boolean checkUrl(String url) {
        //符合标准url
        //不符合标准
        return Patterns.WEB_URL.matcher(url).matches();
    }

}
