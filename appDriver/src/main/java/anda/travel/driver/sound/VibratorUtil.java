package anda.travel.driver.sound;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Vibrator;

/**
 * 手机震动工具类
 */
public class VibratorUtil {
    private static Vibrator mVib;

    // 开启震动
    @SuppressLint("NewApi")
    private static void Vibrate(Context context, long[] pattern,
                                boolean isRepeat) {
        mVib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        boolean hasVibrator = true;
        if (Build.VERSION.SDK_INT >= 11) {
            // API 11以上 监测是否有振动硬件
            hasVibrator = mVib.hasVibrator();
        }
        if (hasVibrator) {
            mVib.vibrate(pattern, isRepeat ? 1 : -1);
        }
    }

    // 关闭震动
    private static void cancel() {
        if (mVib != null) {
            mVib.cancel();
        }
    }

    /**
     * 开启振动（只振动一次）
     *
     * @param context
     */
    public static void vibratorOnce(Context context) {
        vibrator(context, 1);
    }

    /**
     * 开启震动
     *
     * @param context
     * @param sec     需要震动的秒数
     */
    public static void vibrator(Context context, int sec) {
        if (sec <= 0) return;

        cancel(); //先取消
        long[] pattern = new long[2 * sec];
        for (int i = 0; i < 2 * sec; i++) {
            pattern[i] = 500L;
        }
        Vibrate(context, pattern, false);
    }

}
