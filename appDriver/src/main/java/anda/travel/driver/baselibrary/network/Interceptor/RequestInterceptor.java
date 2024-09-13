package anda.travel.driver.baselibrary.network.Interceptor;

import android.text.TextUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import anda.travel.driver.BuildConfig;
import anda.travel.driver.baselibrary.network.RetrofitRequestTool;
import anda.travel.driver.baselibrary.utils.SP;
import anda.travel.driver.baselibrary.utils.VersionUtil;
import anda.travel.driver.baselibrary.utils.security.EncryptionUtil;
import anda.travel.driver.auth.HxClientManager;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import timber.log.Timber;

public class RequestInterceptor implements Interceptor {

    private static final String KEY_APPID = "appid";
    private static final String KEY_NONCESTR = "noncestr";
    private static final String KEY_SIGN = "sign";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_DEVICE_TYPE = "deviceType";
    private static final String KEY_DEVICE_VERSION = "deviceVersion";
    private static final String KEY_APP_VERSION = "appVersion";
    private static final String KEY_PACKAGE_NAME = "packageName";
    private static final String KEY_THREAD_NAME = "threadName";
    private static final String DEVICE_TYPE = "1";
    private final SP mSP;

    public RequestInterceptor(SP SP) {
        mSP = SP;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request original = chain.request();
        SortMap sortMap = new SortMap();

        // 添加头部 token 等
        final Request.Builder builder = chain.request().newBuilder();
        Map<String, String> headers = RetrofitRequestTool.getHeaders(mSP);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }

        //FormBody
        FormBody.Builder newFormBody = new FormBody.Builder();
        if (original.body() instanceof FormBody) {
            FormBody oidFormBody = (FormBody) original.body();
            for (int i = 0; i < oidFormBody.size(); i++) {
                String key = oidFormBody.encodedName(i);
                String value = oidFormBody.encodedValue(i);
                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                    newFormBody.addEncoded(key, value);
                    sortMap.put(URLDecoder.decode(key), URLDecoder.decode(value));
                }
            }
        }

        String requestUrl = original.url().toString();
        // MultipartBody
        MultipartBody.Builder newMultipartBody = null;
        if (original.body() instanceof MultipartBody) {
            MultipartBody oldMultipartBody = (MultipartBody) original.body();
            newMultipartBody = new MultipartBody.Builder();
            if (oldMultipartBody != null) {
                for (int i = 0; i < oldMultipartBody.size(); i++) {
                    MultipartBody.Part part = oldMultipartBody.part(i);

          /*
           上传文件时，请求方法接收的参数类型为RequestBody或MultipartBody.Part参见ApiService文件中uploadFile方法
           RequestBody作为普通参数载体，封装了普通参数的value; MultipartBody.Part即可作为普通参数载体也可作为文件参数载体
           当RequestBody作为参数传入时，框架内部仍然会做相关处理，进一步封装成MultipartBody.Part，因此在拦截器内部，
           拦截的参数都是MultipartBody.Part类型
           */
/*
           1.若MultipartBody.Part作为文件参数载体传入，则构造MultipartBody.Part实例时，
           需使用MultipartBody.Part.createFormData(String name, @Nullable String filename, RequestBody body)方法，
           其中name参数可作为key使用(因为你可能一次上传多个文件，服务端可以此作为区分)且不能为null，
           body参数封装了包括MimeType在内的文件信息，其实例创建方法为RequestBody.create(final @Nullable MediaType contentType, final File file)
           MediaType获取方式如下：
           String fileType = FileUtil.getMimeType(file.getAbsolutePath());
           MediaType mediaType = MediaType.parse(fileType);

           2.若MultipartBody.Part作为普通参数载体，建议使用MultipartBody.Part.createFormData(String name, String value)方法创建Part实例
            name可作为key使用，name不能为null,通过这种方式创建的实例，其RequestBody属性的MediaType为null；当然也可以使用其他方法创建
           */

          /*
           提取非文件参数时,以RequestBody的MediaType为判断依据.
           此处提取方式简单暴力。默认part实例的RequestBody成员变量的MediaType为null时，part为非文件参数
           前提是:
           a.构造RequestBody实例参数时，将MediaType设置为null
           b.构造MultipartBody.Part实例参数时,推荐使用MultipartBody.Part.createFormData(String name, String value)方法，或使用以下方法
            b1.MultipartBody.Part.create(RequestBody body)
            b2.MultipartBody.Part.create(@Nullable Headers headers, RequestBody body)
            若使用方法b1或b2，则要求

           备注：
           您也可根据需求修改RequestBody的MediaType，但尽量保持外部传入参数的MediaType与拦截器内部添加参数的MediaType一致，方便统一处理
           */
                    MediaType mediaType = part.body().contentType();
                    if (mediaType == MediaType.parse("form-data")) {
                        try {
                            Headers contentHeaders = part.headers();
                            if (contentHeaders != null) {
                                for (String name : contentHeaders.names()) {
                                    String headerContent = contentHeaders.get(name);
                                    if (!TextUtils.isEmpty(headerContent)) {
                                        String replaceValue = "form-data; name=";//这段在MultipartBody.Part源码中看到
                                        if (headerContent.contains(replaceValue)) {
                                            String key = headerContent.replace(replaceValue, "").replaceAll("\"", "");
                                            String data = getParamContent(oldMultipartBody.part(i).body());
                                            if (!TextUtils.isEmpty(data)) {
                                                sortMap.put(key, data);
                                                newMultipartBody.addPart(part);
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        newMultipartBody.addPart(part);
                    }
                }
            }
        }

        // appid
        String appId = RetrofitRequestTool.getAppid();
        if (!TextUtils.isEmpty(appId)) {
            sortMap.put(KEY_APPID, appId);
            newFormBody.addEncoded(KEY_APPID, appId);
            if (newMultipartBody != null) newMultipartBody.addFormDataPart(KEY_APPID, appId);
        }

        // nocestr
        String noncestr = String.valueOf(System.currentTimeMillis());
        if (!TextUtils.isEmpty(noncestr)) {
            sortMap.put(KEY_NONCESTR, noncestr);
            newFormBody.addEncoded(KEY_NONCESTR, noncestr);
            if (newMultipartBody != null) newMultipartBody.addFormDataPart(KEY_NONCESTR, noncestr);
        }

        // token
        String token = RetrofitRequestTool.getToken(mSP);
        if (!TextUtils.isEmpty(token)) {
            sortMap.put(KEY_TOKEN, token);
            newFormBody.addEncoded(KEY_TOKEN, token);
            if (newMultipartBody != null) newMultipartBody.addFormDataPart(KEY_TOKEN, token);
        }

        // deviceType
        sortMap.put(KEY_DEVICE_TYPE, DEVICE_TYPE);
        newFormBody.addEncoded(KEY_DEVICE_TYPE, DEVICE_TYPE);
        if (newMultipartBody != null)
            newMultipartBody.addFormDataPart(KEY_DEVICE_TYPE, DEVICE_TYPE);

        // deviceVersion
        String deviceVersion = android.os.Build.MODEL + " -- " + android.os.Build.VERSION.RELEASE; //设备版本
        sortMap.put(KEY_DEVICE_VERSION, deviceVersion);
        newFormBody.addEncoded(KEY_DEVICE_VERSION, deviceVersion);
        if (newMultipartBody != null)
            newMultipartBody.addFormDataPart(KEY_DEVICE_VERSION, deviceVersion);

        // appVersion
        String appVersion = BuildConfig.VERSION_NAME;
        if (!TextUtils.isEmpty(appVersion)) {
            sortMap.put(KEY_APP_VERSION, appVersion);
            newFormBody.addEncoded(KEY_APP_VERSION, appVersion);
            if (newMultipartBody != null)
                newMultipartBody.addFormDataPart(KEY_APP_VERSION, appVersion);
        }

        // package name
        sortMap.put(KEY_PACKAGE_NAME, HxClientManager.getInstance().application.getApplicationContext().getPackageName());
        newFormBody.addEncoded(KEY_PACKAGE_NAME, HxClientManager.getInstance().application.getApplicationContext().getPackageName());
        if (newMultipartBody != null)
            newMultipartBody.addFormDataPart(KEY_PACKAGE_NAME, HxClientManager.getInstance().application.getApplicationContext().getPackageName());

        // thread name
        sortMap.put(KEY_THREAD_NAME, noncestr);
        if (!TextUtils.isEmpty(noncestr)) {
            newFormBody.addEncoded(KEY_THREAD_NAME, noncestr);
            if (newMultipartBody != null)
                newMultipartBody.addFormDataPart(KEY_THREAD_NAME, noncestr);
        }

        // sign
        String sign = getClientSign(sortMap);
        Timber.e("sign: %s", sign);
        if (newMultipartBody != null) newMultipartBody.addFormDataPart(KEY_SIGN, sign);
        else newFormBody.addEncoded(KEY_SIGN, sign);

//        //记录sign，但不用于生成签名
//        sortMap.put(KEY_SIGN, sign);
        // 记录日志
        StringBuilder str = new StringBuilder();
        str.append(requestUrl);
        str.append(" ");
        Timber.e("url=%s", requestUrl);
        if (sortMap.size() > 0) {
            for (String key : sortMap.keySet()) {
                String param = key + "=" + sortMap.get(key);
                str.append(param);
                str.append(" ");
                Timber.e(param);
            }
        }

        builder.method(original.method(), newFormBody.build());
        if (newMultipartBody != null) builder.method(original.method(), newMultipartBody.build());
        return chain.proceed(builder.build());
    }

    /**
     * 获取常规post请求参数
     */
    private String getParamContent(RequestBody body) throws IOException {
        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        return buffer.readUtf8();
    }

    private class SortMap extends TreeMap<String, String> {
        public SortMap() {
            super((obj1, obj2) -> -obj2.compareTo(obj1)); // 升序排序
        }
    }

    private static String getClientSign(Map<String, String> map) {
        StringBuilder params = new StringBuilder();
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            String val = map.get(key);
            params.append(key);
            params.append("=");
            params.append(val);
            params.append("&");
        }
        params.append("key=").append(RetrofitRequestTool.getKey());
        Timber.e("请求参数:%s", params.toString());
        return EncryptionUtil.md5Encode(params.toString()).toUpperCase();
    }
}
