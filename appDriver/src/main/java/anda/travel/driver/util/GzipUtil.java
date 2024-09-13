package anda.travel.driver.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import anda.travel.driver.baselibrary.utils.security.Base64;

/**
 * Created by liuwenwu on 2021/10/18.
 * Des :  Gzip + Base64
 */
public class GzipUtil {

    private static final int BUFFER_SIZE = 1024;

    public static String encryptGZIP(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }

        try {
            // gzip压缩
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(baos);
            gzip.write(str.getBytes("UTF-8"));

            gzip.close();

            byte[] encode = baos.toByteArray();

            baos.flush();
            baos.close();

            // base64 加密
            return Base64.encode(encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * GZIP 解密
     *
     * @param bytes
     * @return
     */
    public static String decryptGZIP(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        try {
            //gzip 解压缩
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            GZIPInputStream gzip = new GZIPInputStream(bais);

            byte[] buf = new byte[BUFFER_SIZE];
            int len = 0;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            while ((len = gzip.read(buf, 0, BUFFER_SIZE)) != -1) {
                baos.write(buf, 0, len);
            }
            gzip.close();
            baos.flush();

            bytes = baos.toByteArray();

            baos.close();

            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
