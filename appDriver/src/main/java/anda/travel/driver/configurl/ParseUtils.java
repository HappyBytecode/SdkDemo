package anda.travel.driver.configurl;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.text.MessageFormat;

import javax.inject.Inject;

import anda.travel.driver.baselibrary.utils.SP;
import anda.travel.driver.auth.HxClientManager;
import timber.log.Timber;

/**
 * 解析配置文件
 */
public class ParseUtils {

    @Inject
    SP mSP;

    private ParseUtils() {
    }

    private static ParseUtils instance;

    public static ParseUtils getInstance() {
        if (instance == null) {
            synchronized (ParseUtils.class) {
                if (instance == null) {
                    instance = new ParseUtils();
                }
            }
        }
        HxClientManager.getAppComponent().inject(instance);
        return instance;
    }

    private MyConfig myConfig;

    public void parse(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return;
            }
            FileInputStream is = new FileInputStream(file);
            PullConfigParser parser = new PullConfigParser();
            myConfig = parser.parse(is);
            // 将结果保存在本地
            mSP.putString("android_config", JSONObject.toJSONString(myConfig));
            Timber.d(MessageFormat.format("------------{0}", myConfig.toString()));
            is.close();
        } catch (Exception e) {
            Timber.e(MessageFormat.format("{0}", e.getMessage()));
        }
    }

    /**
     * 在HomeAty的onCreate中调用
     */
    private void initMyConfig() {
        String data = mSP.getString("android_config");
        if (!TextUtils.isEmpty(data)) {
            try {
                myConfig = JSONObject.parseObject(data, MyConfig.class);
            } catch (Exception e) {
                Timber.e("ParseUtils --- 解析出现异常！");
            }
        }
    }

    public MyConfig getMyConfig() {
        if (myConfig == null) {
            initMyConfig();
        }
        return myConfig;
    }

}
