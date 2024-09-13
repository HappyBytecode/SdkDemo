package anda.travel.driver.baselibrary.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

public class PhoneUtil {

    /**
     * 跳转拨号盘
     */
    public static void skip(Context context, String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL,
                Uri.parse("tel:" + phone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 隐藏中间4位
     *
     * @param mobile
     * @return
     */
    public static String halfCover(String mobile) {
        if (TextUtils.isEmpty(mobile)) {
            return "";
        }
        if (mobile.length() != 11) {
            return mobile;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(mobile.substring(0, 3));
        sb.append("****");
        sb.append(mobile.substring(7));
        return sb.toString();
    }

    /**
     * 跳转拨号盘
     */
    public static void skip(Context context, int resId) {
        String phone = context.getResources().getString(resId);
        skip(context, phone);
    }

    /**
     * 判断是否是手机号
     */
    public static boolean isMobileNO(String mobiles) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
         * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
         * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
         */
        // String telRegex = "[1][34578]\\d{9}";//
        // "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        String telRegex = "[1][3456789]\\d{9}";// 放宽限定。"[1]"代表第1位为数字1，"\\d{10}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles))
            return false;
        else
            return mobiles.matches(telRegex);
    }

    /**
     * 调用系统打电话界面
     *
     * @param phoneNumber 手机号码
     */
    public static void callPhones(Context context, String phoneNumber) {
        skip(context, phoneNumber);
    }
}
