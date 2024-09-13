package anda.travel.driver.util.zip;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import anda.travel.driver.api.ApiConfig;
import anda.travel.driver.data.entity.HtmlVersionEntity;
import anda.travel.driver.event.MessageEvent;
import anda.travel.driver.baselibrary.utils.security.EncryptionUtil;
import timber.log.Timber;

/**
 * @Author: Jotyy
 * @Company: HeXing
 * @Date: 2019/8/14
 * @Desc: zip下载解压工具
 */
public class ZipManager {

    private static final ZipManager instance = new ZipManager();
    private HtmlVersionEntity mHtmlModule;
    private Context mContext;

    private ZipManager() {

    }

    public static ZipManager getInstance() {
        return instance;
    }

    // 压缩文件下载保存目录
    private static final String ZIP_STORE_PATH = Environment
            .getExternalStorageDirectory() + File.separator + "ANDA" + File.separator + "act-html.zip";
    // 解压目录
    public static final String ZIP_UNPACK_PATH = Environment.getExternalStorageDirectory() +
            File.separator + "ANDA" + File.separator + "act-html" + File.separator;

    /**
     * 执行下载解压
     */
    public void zipProcess(Context context, HtmlVersionEntity htmlModule) {
        this.mContext = context;
        this.mHtmlModule = htmlModule;
        new ZipProcessTask().execute();
    }

    private class ZipProcessTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
        }

        protected String doInBackground(String... params) {
            return download();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected void onPostExecute(String result) {
            Timber.i("ZipManager----> 解压完成");
        }
    }

    /**
     * 下载并解压
     *
     * @return null
     */
    private String download() {
        String downloadUrl, password;
        // 先判断 H5文件夹 是否已存在
        String h5Path = mContext.getFilesDir().getAbsolutePath() + "/html/" + mHtmlModule.getModuleCode() + "/index.html";
        File h5File = new File(h5Path);
        if (h5File.exists() && mHtmlModule.getUpdType() == 2) { // h5文件已存在 且本次更新为增量更新
            // 增量下载
            downloadUrl = ApiConfig.H5_DOWNLOAD_URL + mHtmlModule.getUpdPackagePath();
            password = EncryptionUtil.md5Encode(mHtmlModule.getUpdPackagePath()).toLowerCase();
        } else {
            // 全量下载
            downloadUrl = ApiConfig.H5_DOWNLOAD_URL + mHtmlModule.getPackagePath();
            password = EncryptionUtil.md5Encode(mHtmlModule.getPackagePath()).toLowerCase();
        }
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            URL url = new URL(downloadUrl);
            SSLSocketFactory sslSocketFactory = setCertificates();
            HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
            HttpURLConnection conn = (HttpURLConnection) url
                    .openConnection();
            // 设置超时时间
            conn.setConnectTimeout(25000);
            conn.setReadTimeout(25000);
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }
            is = conn.getInputStream();
            int len;
            byte[] buffer = new byte[2048];
            File zipFile = new File(mContext.getFilesDir().getAbsolutePath() + File.separator + "act-html.zip");
            if (!zipFile.exists()) {
                // 如果文件不存在，说明是第一次下载
                // 需要先创建文件夹
                zipFile.getParentFile().mkdirs();
            }
            fos = new FileOutputStream(mContext.getFilesDir().getAbsolutePath() + File.separator + "act-html.zip");
            while (-1 != (len = is.read(buffer))) {
                fos.write(buffer, 0, len);
            }
            unZipFile(zipFile, password);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 解压文件
     *
     * @param zipFile  zip文件
     * @param password 解压密码
     */
    private void unZipFile(File zipFile, String password) {
        String path = mContext.getFilesDir().getAbsolutePath() + "/html/" + mHtmlModule.getModuleCode() + "/";
//        String path = ZIP_UNPACK_PATH + "/html/" + mHtmlModule.getModuleCode() + "/";
        ZipFile zFile = new ZipFile(zipFile, password.toCharArray());
        try {
            zFile.extractAll(path);
        } catch (ZipException e) {
            EventBus.getDefault().post(new MessageEvent(MessageEvent.ACT_HTML_ON));
            e.printStackTrace();
        } finally {
            // 解压完成后删除zip包
//            zipFile.delete();
        }
    }

    //设置证书
    private static SSLSocketFactory setCertificates() {
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
            sslContext.init(null, trustAllCertManagers, new SecureRandom());

            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
