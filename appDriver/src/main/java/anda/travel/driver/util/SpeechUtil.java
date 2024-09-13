package anda.travel.driver.util;

import static anda.travel.driver.util.voice.MainHandlerConstant.IS_END;
import static anda.travel.driver.util.voice.MainHandlerConstant.IS_SPEAKING;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import anda.travel.driver.sound.SoundUtils;
import anda.travel.driver.util.voice.control.NonBlockSyntherizer;

public class SpeechUtil {

    private static Handler mainHandler;
    private static boolean isSpeaking = false;
    private static SpeechFinish speechFinish;
    public static Context mContext;
    public static HandlerThread hThread;

    private static void handle(Message msg) {
        switch (msg.what) {
            case IS_SPEAKING:
                isSpeaking = true;
                break;
            case IS_END:
                isSpeaking = false;
                if (speechFinish != null) speechFinish.speechFinish();
                break;
            case 3:
                /////放在子线程中播放语音
                NonBlockSyntherizer.getDefault(mContext, mainHandler).speak((String) msg.obj);
                break;
            default:
                break;
        }
    }

    /**
     * 初始化百度语音，一般在Application中调用
     *
     * @param context
     */
    public static void init(Context context) {
        mContext = context;
        hThread = new HandlerThread("");
        hThread.start();
        mainHandler = new Handler(hThread.getLooper()) {
            /*
             * @param msg
             */
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                handle(msg);
            }
        };

        NonBlockSyntherizer.getDefault(mContext, mainHandler);
    }

    /**
     * 播放语音
     *
     * @param
     * @param strRes
     */
    public static void speech(int strRes) {
        String str = mContext.getResources().getString(strRes);
        speech(mContext, str);
    }

    public static void speech(String string, SpeechFinish callback) {
        speech(string);
        speechFinish = callback;
    }

    public static void speech(String string) {
        speech(mContext, string);
    }

    /**
     * 播放语音
     *
     * @param context
     * @param string
     */
    public static void speech(Context context, String string) {
        if (speechFinish != null) speechFinish = null;
        //暂停其它语音播报
        SoundUtils.getInstance().stop();
        if (isSpeaking()) {
            //先结束之前的语音
            stop();
        }
        //设置音量
        //VolumeUtils.setVolume(context);
//        SP mSP = SP.getInstance(context);
//        if (mSP.getBoolean(IConstants.VOLUME_TYPE)) { //固定音量播报
//            int size = mSP.getInt(IConstants.IMMOBILIZATION_VOLUME_SIZE);
//            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//            if (size == 0) size = manager.getStreamMaxVolume(STREAM_MUSIC);
//            manager.setStreamVolume(STREAM_MUSIC, size, 0); //设置音量
//        }
//
//        //1.创建 SpeechSynthesizer 对象, 第二个参数:本地合成时传 InitListener
//        SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(context, null);
//        //2.合成参数设置,详见《MSC Reference Manual》SpeechSynthesizer 类
//        // 设置发音人(更多在线发音人,用户可参见 附录13.2
//        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
//        // 设置发音人
//        mTts.setParameter(SpeechConstant.SPEED, "80");//设置语速
//        mTts.setParameter(SpeechConstant.VOLUME, "100");
//        // 设置音量,范围 0~100
//        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
//        // 设置云端
//        // 设置合成音频保存位置(可自定义保存位置),保存在“./sdcard/iflytek.pcm”
//        //保存在 SD 卡需要在 AndroidManifest.xml 添加写 SD 卡权限
//        // 仅支持保存为 pcm 和 wav 格式,如果不需要保存合成音频,注释该行代码
//        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
//        //3.开始合成
//        mTts.startSpeaking(string, mSynListener);
        //        MySyntherizer.getDefault(context).speak(str);

        //new Thread(() -> NonBlockSyntherizer.getDefault(context,mainHandler).speak(string)).start();

        Message msg = Message.obtain();
        msg.what = 3;
        msg.obj = string;
        mainHandler.sendMessage(msg);
    }

    // 是否正在播报语音
    public static boolean isSpeaking() {
//        SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(context, null);
        return isSpeaking;
    }

    // 停止语音播报
    public static void stop() {
        //1.创建 SpeechSynthesizer 对象, 第二个参数:本地合成时传 InitListener
//        SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(context, null);
//        // 如果正在播报，则关闭语音播报
//        if (mTts.isSpeaking()) {
//            mTts.stopSpeaking();
//        }
//        MySyntherizer.getDefault(context).stop();
        NonBlockSyntherizer.getDefault(mContext, mainHandler).stop();
    }

    public static void pause(Context context) {
        NonBlockSyntherizer.getDefault(context, mainHandler).pause();
    }

    public static void resume(Context context) {
        NonBlockSyntherizer.getDefault(context, mainHandler).resume();
    }

    public interface SpeechFinish {
        void speechFinish();
    }

}
