package anda.travel.driver.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.provider.Settings;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import anda.travel.driver.api.DriverApi;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.baselibrary.utils.file.FileUtil;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

/**
 * 异常处理
 */
@Singleton
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    //异常信息
    private static final String EXCEPETION_INFOS_STRING = "EXCEPETION_INFOS_STRING";
    //应用包信息
    private static final String PACKAGE_INFOS_MAP = "PACKAGE_INFOS_MAP";
    //设备数据信息
    private static final String BUILD_INFOS_MAP = "BUILD_INFOS_MAP";
    //系统常规配置信息
    private static final String SYSTEM_INFOS_MAP = "SYSTEM_INFOS_MAP";
    //手机安全配置信息
    private static final String SECURE_INFOS_MAP = "SECURE_INFOS_MAP";
    private static final String VERSION_NAME = "versionName";
    private static final String VERSION_CODE = "versionCode";
    //用来存储设备信息和异常信息
    private final ConcurrentHashMap<String, Object> infos = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> mPackageInfos = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> mDeviceInfos = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> mSystemInfos = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> mSecureInfos = new ConcurrentHashMap<>();

    //文件名规范
    private static final String PREFIX_CRASH = "android_crash_";
    private static final String FORMAT_FILE_NAME = "yyyy_MM_dd_HH_mm_ss";
    //日志的保存地址
    private static final String ANALYZE_FILE_PATH = Environment.getExternalStorageDirectory() +
            File.separator + "ANDA" + File.separator + "log" + File.separator;

    //系统默认的异常处理（默认情况下，系统会终止当前的异常程序）
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;

    private final Context mContext;

    //构造方法私有，防止外部构造多个实例，即采用单例模式
    @Inject
    public CrashHandler(Context context) {
        mContext = context;
    }

    //这里主要完成初始化工作
    public void init() {
        //获取系统默认的异常处理器
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        //将当前实例设为系统默认的异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 自定义处理异常的方法
     * 发送错误数据到服务器
     *
     * @param ex
     */
    private Boolean handleException(Throwable ex) {
        if (ex == null) return false;
        // 收集异常信息
        collectInfos(mContext, ex);
        long lastTime = System.currentTimeMillis();
        // 保存信息到文件并上传
        saveCrashToFile(lastTime);
        return true;
    }

    /**
     * 获取设备参数信息
     *
     * @param context
     */
    private void collectInfos(Context context, Throwable ex) {
        String mExceptionInfos = collectExceptionInfos(ex);
        collectPackageInfos(context);
        collectBuildInfos();
        collectSystemInfos();
        collectSecureInfos();

        //将信息储存到一个总的Map中提供给上传动作回调
        infos.put(EXCEPETION_INFOS_STRING, mExceptionInfos);
        infos.put(PACKAGE_INFOS_MAP, mPackageInfos);
        infos.put(BUILD_INFOS_MAP, mDeviceInfos);
        infos.put(SYSTEM_INFOS_MAP, mSystemInfos);
        infos.put(SECURE_INFOS_MAP, mSecureInfos);
    }

    /**
     * 获取捕获异常的信息
     *
     * @param ex
     */
    private String collectExceptionInfos(Throwable ex) {
        Writer mWriter = new StringWriter();
        PrintWriter mPrintWriter = new PrintWriter(mWriter);
        ex.printStackTrace(mPrintWriter);
        ex.printStackTrace();
        Throwable mThrowable = ex.getCause();
        // 迭代栈队列把所有的异常信息写入writer中
        while (mThrowable != null) {
            mThrowable.printStackTrace(mPrintWriter);
            // 换行 每个个异常栈之间换行
            mPrintWriter.append("\r\n");
            mThrowable = mThrowable.getCause();
        }
        // 记得关闭
        mPrintWriter.close();
        return mWriter.toString();
    }

    /**
     * 获取应用包参数信息
     */
    private void collectPackageInfos(Context context) {
        try {
            // 获得包管理器
            PackageManager mPackageManager = context.getPackageManager();
            // 得到该应用的信息，即主Activity
            PackageInfo mPackageInfo = mPackageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (mPackageInfo != null) {
                String versionName = mPackageInfo.versionName == null ? "null" : mPackageInfo.versionName;
                String versionCode = mPackageInfo.versionCode + "";
                mPackageInfos.put(VERSION_NAME, versionName);
                mPackageInfos.put(VERSION_CODE, versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从系统属性中提取设备硬件和版本信息
     */
    private void collectBuildInfos() {
        // 反射机制
        Field[] mFields = Build.class.getDeclaredFields();
        // 迭代Build的字段key-value 此处的信息主要是为了在服务器端手机各种版本手机报错的原因
        for (Field field : mFields) {
            try {
                field.setAccessible(true);
                mDeviceInfos.put(field.getName(), field.get("").toString());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取系统常规设定属性
     */
    private void collectSystemInfos() {
        Field[] fields = Settings.System.class.getFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Deprecated.class)
                    && field.getType() == String.class) {
                try {
                    String value = Settings.System.getString(mContext.getContentResolver(), (String) field.get(null));
                    if (value != null) {
                        mSystemInfos.put(field.getName(), value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取系统安全设置信息
     */
    private void collectSecureInfos() {
        Field[] fields = Settings.Secure.class.getFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Deprecated.class)
                    && field.getType() == String.class
                    && field.getName().startsWith("WIFI_AP")) {
                try {
                    String value = Settings.Secure.getString(mContext.getContentResolver(), (String) field.get(null));
                    if (value != null) {
                        mSecureInfos.put(field.getName(), value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Subscription mLogSub;

    private void saveCrashToFile(Long lastTime) {
        if (mLogSub != null) mLogSub.unsubscribe();
        mLogSub = Observable.just("")
                .map(s -> {
                    StringBuffer mStringBuffer = getInfosStr(mPackageInfos);
                    mStringBuffer.append(infos);
                    // 保存文件，设置文件名
                    String fileName = PREFIX_CRASH + new SimpleDateFormat(FORMAT_FILE_NAME).format(lastTime);
                    String filePath = ANALYZE_FILE_PATH + fileName + ".txt"; //文件保存的地址
                    boolean isWritten = FileUtil.writeFile(filePath, mStringBuffer.toString());
                    Timber.e("----->isWritten:" + filePath + isWritten);
                    return lastTime;
                }).compose(RxUtil.applySchedulers())
                .subscribe();
    }

    /**
     * 将HashMap遍历转换成StringBuffer
     */
    @NonNull
    private static StringBuffer getInfosStr(ConcurrentHashMap<String, String> infos) {
        StringBuffer mStringBuffer = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            mStringBuffer.append(key + "=" + value + "\r\n");
        }
        return mStringBuffer;
    }

    /**
     * 当程序中有未被捕获的异常，系统将会自动调用#uncaughtException方法
     * thread为出现未捕获异常的线程，ex为未捕获的异常，有了这个ex，我们就可以得到异常信息。
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        //如果系统提供了默认的异常处理器，则交给系统去结束我们的程序，否则就由我们自己结束自己
        if (!handleException(ex) && mDefaultCrashHandler != null) {
            mDefaultCrashHandler.uncaughtException(thread, ex);
        } else {
            killProcess();
        }
    }

    /**
     * 退出应用
     */
    private static void killProcess() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Timber.e("CrashHandler.InterruptedException--->" + ex.toString());
        }
        //退出程序
        Process.killProcess(Process.myPid());
        System.exit(1);
    }

}
