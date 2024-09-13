package anda.travel.driver.baselibrary.network;

import android.text.TextUtils;

import java.io.File;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import anda.travel.driver.baselibrary.network.Interceptor.RequestInterceptor;
import anda.travel.driver.baselibrary.network.converter.FastJsonConverterFactory;
import anda.travel.driver.baselibrary.utils.SP;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import timber.log.Timber;

public class RetrofitUtil {

    private RetrofitUtil() {

    }

    /**
     * @param tClass retrofitAPI 类
     * @param host   服务器地址（必须以 / 结尾）
     * @param sp     SharePreference 用于保存token、header等
     * @return
     */
    public static <T> T getService(Class<T> tClass, String host, SP sp) {
        SSLSocketFactory sslSocketFactory = setCertificates();
        if (TextUtils.isEmpty(host)) {
            host = "http://localhost/";
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(host)
                .client(getOkHttpClient(sp, sslSocketFactory))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // 添加 RxJava 适配器
                .addConverterFactory(FastJsonConverterFactory.create())  // 添加 fastJson 解析器
                .build();
        return retrofit.create(tClass);
    }

    private static OkHttpClient okHttpClient = null;

    // 获得OKhttpClient
    public static OkHttpClient getOkHttpClient(SP sp, SSLSocketFactory sslSocketFactory) {
        if (okHttpClient == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(
                    message -> Timber.tag("OkHttp").i(message)
            );
            logging.level(HttpLoggingInterceptor.Level.BODY);
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new RequestInterceptor(sp))
                    .addInterceptor(logging)
                    .hostnameVerifier((s, sslSession) -> true)
                    .sslSocketFactory(sslSocketFactory, new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {

                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {

                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    })
                    .build();
            return okHttpClient;
        }
        return okHttpClient;
    }

    public static <T> T getRecordingService(Class<T> tClass, String host, SP sp) {
        SSLSocketFactory sslSocketFactory = setCertificates();
        if (TextUtils.isEmpty(host)) {
            host = "http://localhost/";
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(host)
                .client(getRecordingClient(sp, sslSocketFactory))
                .addConverterFactory(FastJsonConverterFactory.create())  // 添加 fastJson 解析器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // 添加 RxJava 适配器
                .build();
        return retrofit.create(tClass);
    }

    // 获得OKhttpClient
    public static OkHttpClient getRecordingClient(SP sp, SSLSocketFactory sslSocketFactory) {
        if (okHttpClient == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(
                    message -> Timber.tag("OkHttp").i(message)
            );
            logging.level(HttpLoggingInterceptor.Level.BODY);
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new RequestInterceptor(sp))
                    .addInterceptor(logging)
                    .connectTimeout(2, TimeUnit.MINUTES).writeTimeout(2, TimeUnit.MINUTES)
                    .hostnameVerifier((s, sslSession) -> true)
                    .sslSocketFactory(sslSocketFactory, new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {

                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {

                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    })
                    .build();
            return okHttpClient;
        }
        return okHttpClient;
    }

    /**
     * Retrofit 使用 OkHttp 上传时的 RequestBody
     *
     * @param value 参数
     * @return
     */
    public static RequestBody getRequestBody(String value) {
        return RequestBody.create(MediaType.parse("form-data"), value);
    }

    /**
     * Retrofit 使用 OkHttp 上传时的 file 包装
     *
     * @param key  参数
     * @param file 文件名
     * @return
     */
    public static MultipartBody.Part getRequestPart(String key, File file) {
        RequestBody fileBody = MultipartBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData(key, file.getName(), fileBody);
    }

    /**
     * @param key
     * @param file
     * @return
     */
    public static MultipartBody.Part getRequestPartFile(String key, File file) {
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/otcet-stream"), file);
        return MultipartBody.Part.createFormData(key, file.getName(), fileBody);
    }

    /**
     * @param key   参数
     * @param files 文件名列表
     * @return
     */
    public static MultipartBody.Part[] getRequestParts(String key, File... files) {
        MultipartBody.Part[] parts = new MultipartBody.Part[files.length];
        for (int i = 0; i < files.length; i++) {
            parts[i] = getRequestPart(key, files[i]);
        }
        return parts;
    }

    public static MultipartBody.Part[] getAudioRequestParts(String key, File... files) {
        MultipartBody.Part[] parts = new MultipartBody.Part[files.length];
        for (int i = 0; i < files.length; i++) {
            parts[i] = getAudioRequestPart(key, files[i]);
        }
        return parts;
    }

    public static MultipartBody.Part getAudioRequestPart(String key, File file) {
        RequestBody fileBody = MultipartBody.create(MediaType.parse("audio/*"), file);
        return MultipartBody.Part.createFormData(key, file.getName(), fileBody);
    }

    //设置证书
    public static SSLSocketFactory setCertificates() {
        try {
            TrustManager[] trustAllCertManagers = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }
            }};
            //创建一个使用我们信任管理器的SSLContext
            SSLContext sslContext = SSLContext.getInstance("SSL");
//            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());    //双向认证
            sslContext.init(null, trustAllCertManagers, new SecureRandom());    //单向认证
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
