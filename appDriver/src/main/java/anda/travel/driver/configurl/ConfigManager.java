package anda.travel.driver.configurl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import anda.travel.driver.baselibrary.network.RequestParams;
import anda.travel.driver.baselibrary.utils.SP;
import anda.travel.driver.common.AppConfig;
import anda.travel.driver.util.SignUtils;
import timber.log.Timber;

/**
 * 用于下载配置文件
 */
public final class ConfigManager {

    public Context mContext;

    private final String CONFIG_PATH = Environment
            .getExternalStorageDirectory() + File.separator +
            "ANDA" + File.separator + "config.xml";

    public ConfigManager(Context context) {
        mContext = context;
    }

    private static final int Complete = 1;
    private static final int Fail = 2;

    // 为避免2.3系统上，onPostExecute()有可能不执行添加
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == Complete) {
                // 下载成功，解析配置文件
                ParseUtils.getInstance().parse(CONFIG_PATH);
            }
        }
    };

    public void downloadFile(String configUrl, String packageName) {
        if (TextUtils.isEmpty(configUrl)) return;
        new DownloadTask().execute(configUrl, packageName);
    }

    //设置证书
    private static SSLSocketFactory setCertificates() {
        try {
            TrustManager[] trustAllCertManagers = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }
            }};
            //创建一个使用我们信任管理器的SSLContext
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCertManagers, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private final SSLSocketFactory defaultSSLSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();

    @SuppressLint("StaticFieldLeak")
    class DownloadTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
        }

        protected String doInBackground(String... params) {
            InputStream is = null;
            try {
                String noncestr = String.valueOf(System.currentTimeMillis());
                String getUrl = params[0]
                        + "?appid=" + URLEncoder.encode(AppConfig.ANDA_APPKEY, "utf-8")
                        + "&noncestr=" + noncestr
                        + "&sign=" + SignUtils.getSign(new RequestParams.Builder().putParam("noncestr", noncestr).build());
                Timber.e(getUrl);
                URL url = new URL(getUrl);
                SSLSocketFactory sslSocketFactory = setCertificates();
                HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 设置超时时间
                conn.setConnectTimeout(25000);
                conn.setReadTimeout(25000);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                int responseCode = conn.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return null;
                }
                is = conn.getInputStream();
                PullConfigParser parser = new PullConfigParser();
                SP.getInstance(mContext).putString("android_config", JSONObject.toJSONString(parser.parse(is)));
            } catch (IOException e) {
                handler.sendEmptyMessage(Fail);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            HttpsURLConnection.setDefaultSSLSocketFactory(defaultSSLSocketFactory);
        }
    }
}
