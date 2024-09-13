package anda.travel.driver.sound;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import anda.travel.driver.util.SpeechUtil;

public class SoundUtils {
    // 语音操作对象
    private MediaPlayer mPlayer;
    private Context mContext;
    private boolean isPause;
    ExecutorService singleThreadPool; /////创建单一线程池

    /**
     * 需要在application中初始化
     */
    public void init(Context appContext) {
        mContext = appContext;
        singleThreadPool = Executors.newSingleThreadExecutor();
    }

    private SoundUtils() {

    }

    private static SoundUtils instance;

    public static SoundUtils getInstance() {
        if (instance == null) {
            synchronized (SoundUtils.class) {
                if (instance == null) {
                    instance = new SoundUtils();
                }
            }
        }
        return instance;
    }

    // 播放默认语音
    public void play() {
        play(0);
    }

    // 停止播放
    public void stop() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void play(int soundRes) {
        singleThreadPool.execute(() -> {
            stop();
            try {
                // create后就会处于prepare状态，所以不需要调用异步操作来等它prepared
                if (soundRes != 0) {
                    mPlayer = MediaPlayer.create(mContext, soundRes);
                } else {
                    mPlayer = MediaPlayer.create(mContext, getSystemDefultRingtoneUri());
                }
                mPlayer.setOnCompletionListener(mp -> {
                });
                mPlayer.start();
            } catch (Exception e) {
                mPlayer = null;
            }
        });
    }

    public void play(int soundRes, SoundPlayFinish soundPlayFinish) {
        singleThreadPool.execute(() -> {
            if (SpeechUtil.isSpeaking()) {
                SpeechUtil.pause(mContext);
                isPause = true;
            }
            //设置音量
            //VolumeUtils.setVolume(HxClientManager.getInstance().application.getApplicationContext());
            stop();
            try {
                // create后就会处于prepare状态，所以不需要调用异步操作来等它prepared
                if (soundRes != 0) {
                    mPlayer = MediaPlayer.create(mContext, soundRes);
                } else {
                    mPlayer = MediaPlayer.create(mContext, getSystemDefultRingtoneUri());
                }
                mPlayer.setOnCompletionListener(mp -> {
                    if (isPause) {
                        SpeechUtil.resume(mContext);
                        isPause = false;
                    }
                    stop();
                    if (soundPlayFinish != null) {
                        soundPlayFinish.playFinish();
                    }
                });
                mPlayer.start();
            } catch (Exception e) {
                mPlayer = null;
            }
        });
    }

    private Uri getSystemDefultRingtoneUri() {
        return RingtoneManager.getActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_NOTIFICATION);
    }

    public interface SoundPlayFinish {
        void playFinish();
    }

}
