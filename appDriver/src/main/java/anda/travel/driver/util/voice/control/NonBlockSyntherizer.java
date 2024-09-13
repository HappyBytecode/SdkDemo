package anda.travel.driver.util.voice.control;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import timber.log.Timber;

/**
 * 在新线程中调用initTTs方法。防止UI柱塞
 * <p>
 * Created by fujiayi on 2017/5/24.
 */

public class NonBlockSyntherizer extends MySyntherizer {

    private static final int INIT = 1;

    private static final int RELEASE = 11;
    private static volatile NonBlockSyntherizer defaultInstance;
    private HandlerThread hThread;

    private static final String TAG = "NonBlockSyntherizer";

    /**
     * Convenience singleton for apps using a process-wide EventBus instance.
     */
    public static NonBlockSyntherizer getDefault(Context context, Handler mainHandler) {
        if (defaultInstance == null) {
            synchronized (MySyntherizer.class) {
                if (defaultInstance == null) {
                    defaultInstance = new NonBlockSyntherizer(context, mainHandler);
                }
            }
        }
        return defaultInstance;
    }

    private NonBlockSyntherizer(Context context, Handler mainHandler) {
        super(context, mainHandler);
        initThread();
    }

    private void initThread() {
        hThread = new HandlerThread("NonBlockSyntherizer-thread");
        hThread.start();
        // speak("初始化成功");
        Handler tHandler = new Handler(hThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case INIT:
                        InitConfig config = (InitConfig) msg.obj;
                        boolean isSuccess = init(config);
                        if (isSuccess) {
                            // speak("初始化成功");
                            Timber.e("NonBlockSyntherizer 初始化成功");
                        } else {
                            Timber.e("合成引擎初始化失败, 请查看日志");
                        }
                        break;
                    case RELEASE:
                        NonBlockSyntherizer.super.release();
                        if (Build.VERSION.SDK_INT < 18) {
                            getLooper().quit();
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public void release() {
        if (Build.VERSION.SDK_INT >= 18) {
            hThread.quitSafely();
        }
    }

}
