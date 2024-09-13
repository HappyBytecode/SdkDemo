package anda.travel.driver.baselibrary.network;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import anda.travel.driver.baselibrary.utils.SP;

public class RetrofitRequestTool {

    private static Map<String, String> mHeaders;
    private static final String REQUEST_HEADERS = "REQUEST_HEADERS";
    private static final String SPLITER = "--Anda--";
    private static final String KEY_TOKEN = "RetrofitRequestTool#KEY_TOKEN";

    private static String mAppid;
    private static String key;
    private static String token;

    public static String getAppid() {
        return mAppid;
    }

    public static void setAppid(String appid) {
        mAppid = appid;
    }

    public static String getKey() {
        return key;
    }

    public static void setKey(String key) {
        RetrofitRequestTool.key = key;
    }

    public static void saveToken(SP sp, String token) {
        RetrofitRequestTool.token = token;
        sp.putString(KEY_TOKEN, token);
    }

    public static String getToken(SP sp) {
        if (!TextUtils.isEmpty(token)) {
            return token;
        }
        token = sp.getString(KEY_TOKEN, null);
        return token;
    }

    public static Map<String, String> getHeaders(SP sp) {
        initHeaders(sp);
        return mHeaders;
    }

    public static void addHeader(SP sp, String key, String value) {
        initHeaders(sp);
        mHeaders.put(key, value);
        save(sp, mHeaders);
    }

    public static String getHeader(SP sp, String key) {
        initHeaders(sp);
        return mHeaders.get(key);
    }

    public static void setHeaders(SP sp, Map<String, String> headers) {
        initHeaders(sp);
        mHeaders.clear();
        for (String key : headers.keySet()) {
            mHeaders.put(key, headers.get(key));
        }
        save(sp, mHeaders);
    }

    public static void remove(SP sp, String key) {
        initHeaders(sp);
        if (mHeaders.containsKey(key)) {
            mHeaders.remove(key);
            save(sp, mHeaders);
        }
    }

    public static void removeAll(SP sp) {
        initHeaders(sp);
        mHeaders.clear();
        save(sp, mHeaders);
    }

    private static void save(SP sp, Map<String, String> headers) {
        Set<String> strings = new HashSet<>();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            strings.add(entry.getKey() + SPLITER + entry.getValue());
        }
        sp.putStringSet(REQUEST_HEADERS, strings);
    }

    private static void initHeaders(SP sp) {
        if (mHeaders == null) {
            mHeaders = new HashMap<>();
            Set<String> strings = sp.getStringSet(REQUEST_HEADERS);
            for (String string : strings) {
                String[] sts = string.split(SPLITER);
                if (sts.length > 1) {
                    mHeaders.put(sts[0], sts[1]);
                }
            }
        }
    }
}
