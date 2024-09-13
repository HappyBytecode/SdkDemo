package anda.travel.driver.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import anda.travel.driver.common.AppConfig;
import timber.log.Timber;

/**
 * 功能描述：签名算法工具类
 */
public class SignUtils {

    /**
     * 1. 参数名ASCII码从小到大排序（字典序）；
     * 2. 如果参数的值为空不参与签名；
     * 3. 参数名区分大小写；
     * 4. 传送的sign参数不参与签名；
     * 5. API密钥拼接在最后。
     *
     * @param params
     * @return
     */
    public static String getSign(HashMap<String, String> params) {
        params.put("appid", AppConfig.ANDA_APPKEY);
        //params.put("noncestr", "1");

        ArrayList<String> keys = new ArrayList<>(params.keySet());
        // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
        Collections.sort(keys, (o1, o2) -> (o1).compareTo(o2));

        StringBuilder str = new StringBuilder();
        for (String key : keys) {
            str.append(key);
            str.append("=");
            str.append(params.get(key));
            str.append("&");
        }
        str.append("key=");
        str.append(AppConfig.REQUEST_KEY);
        String stringSignTemp = str.toString();

        Timber.e("stringSignTemp = " + stringSignTemp);

        // MD5加密，并将小写字母转成大写
        // return Md5Utility.getStringMD5(stringSignTemp).toUpperCase();
        return MD5Utils.MD5Encode(stringSignTemp);
    }

}
