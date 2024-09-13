package anda.travel.driver.socket.utils;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import anda.travel.driver.R;
import anda.travel.driver.config.DutyStatus;
import anda.travel.driver.configurl.MyConfig;
import anda.travel.driver.configurl.ParseUtils;
import anda.travel.driver.data.duty.DutyRepository;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.module.main.MainActivity;
import anda.travel.driver.module.web.WebActivity;
import anda.travel.driver.sound.SoundUtils;
import anda.travel.driver.sound.VibratorUtil;
import anda.travel.driver.baselibrary.utils.BackgroundUtil;
import anda.travel.driver.baselibrary.utils.NetworkUtil;

/**
 * @Author moyuwan
 * @Date 17/12/6
 * <p>
 * "网络受限提示"工具类
 */
public class NetLimitUtils {

    private final static int CONNECT_ERROR = 2001; //通知id

    /**
     * 检查网络是否受限
     *
     * @param context
     * @param mUserRepository
     * @param mDutyRepository
     */
    public static void checkConnect(Context context, UserRepository mUserRepository, DutyRepository mDutyRepository) {
        if (NetworkUtil.isNetworkAvailable(context) //数据流量或网络可用
                && mUserRepository.isLogin()) { //已登录

            if (mDutyRepository.getIsOnDuty() == DutyStatus.ON_DUTY_INT //听单中
                    || mDutyRepository.getIsOrderOngoing()) { //或 订单进行中

                if ((!BackgroundUtil.isScreenOn(context)) //锁屏状态
                        || BackgroundUtil.isBackground(context)) { //或 应用处于后台
                    showNoticeWithVibratorAndSound(context);
                }
            }
        }
    }

    private static void showNoticeWithVibratorAndSound(Context context) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.mipmap.hxyc_ic_launcher);
        builder.setContentTitle("应用网络受限");
        builder.setContentText("点击打开应用");
        builder.setAutoCancel(true);

        Intent intent;
        MyConfig config = ParseUtils.getInstance().getMyConfig();
        if (config != null && !TextUtils.isEmpty(config.getSolution())) {
            intent = WebActivity.getIntent(context, config.getSolution(), "说明");
        } else {
            intent = new Intent(context, MainActivity.class);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        builder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        manager.notify(CONNECT_ERROR, builder.build());

        VibratorUtil.vibrator(context, 4); //4秒震动提醒
        SoundUtils.getInstance().play(R.raw.network_limit); //播放提示音
    }
}
