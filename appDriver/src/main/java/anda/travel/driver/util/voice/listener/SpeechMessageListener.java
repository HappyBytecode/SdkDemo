package anda.travel.driver.util.voice.listener;

import android.os.Handler;
import android.os.Message;

import timber.log.Timber;

/**
 * 在 MessageListener的基础上，和UI配合。
 * Created by fujiayi on 2017/9/14.
 */

public class SpeechMessageListener extends MessageListener {

    private final Handler mainHandler;

    private static final String TAG = "UiMessageListener";

    public SpeechMessageListener(Handler mainHandler) {
        super();
        this.mainHandler = mainHandler;
    }

    @Override
    public void onSpeechStart(String utteranceId) {
        sendMessage("start", false, IS_SPEAKING);
    }

    /**
     * 播放进度回调接口，分多次回调
     * 注意：progress表示进度，与播放到哪个字无关
     *
     * @param utteranceId
     * @param progress    文本按字符划分的进度，比如:你好啊 进度是0-3
     */
    @Override
    public void onSpeechProgressChanged(String utteranceId, int progress) {
    }

    @Override
    public void onSpeechFinish(String utteranceId) {
        sendMessage("end", false, IS_END);
    }

    protected void sendMessage(String message) {
        sendMessage(message, false);
    }

    @Override
    protected void sendMessage(String message, boolean isError) {
        sendMessage(message, isError, PRINT);
    }

    private void sendMessage(String message, boolean isError, int action) {
        super.sendMessage(message, isError);
        if (mainHandler != null) {
            Message msg = Message.obtain();
            msg.what = action;
            msg.obj = message + "\n";
            mainHandler.sendMessage(msg);
            Timber.i(message);
        }
    }
}
