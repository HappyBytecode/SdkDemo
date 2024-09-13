package anda.travel.driver.service

import anda.travel.driver.auth.HxClientManager
import anda.travel.driver.config.IConstants
import anda.travel.driver.util.OssManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import com.czt.mp3recorder.MP3Recorder
import rx.subscriptions.CompositeSubscription
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*

class RecordingService : Service() {

    companion object {
        private var mInstance: RecordingService? = null

        fun startService(context: Context, foldPath: String) {
            val intent = Intent(context, RecordingService::class.java)
            intent.putExtra("fold", foldPath)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun getInstance(): RecordingService? {
            return mInstance
        }
    }

    fun stopService() {
        stopSelf()
    }

    private var mRecorder: MP3Recorder? = null
    private var mFileName: String? = null
    private var mFilePath: String? = null
    private var mStartingTimeMillis: Long = 0
    private var mElapsedMillis: Long = 0
    private var mOrderUuid: String? = null
    private var mDisposable = CompositeSubscription()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.getStringExtra("fold")?.let {
            startRecording(it)
            mOrderUuid = it
        }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        //适配8.0service
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        var mChannel: NotificationChannel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = NotificationChannel("hxyc", "和行约车", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(mChannel)
            val notification = Notification.Builder(applicationContext, "hxyc").build()
            startForeground(1, notification)
        }
        HxClientManager.getAppComponent().inject(this)
        mInstance = this
    }

    override fun onDestroy() {
        Timber.d("录音服务销毁")
        mDisposable.clear()
        super.onDestroy()
    }

    private fun startRecording(filePath: String) {
        Handler().post {
            setFileNameAndPath(filePath)
            try {
                mRecorder = MP3Recorder(File(mFilePath))
                mRecorder?.run {
                    mRecorder?.start()
                    mStartingTimeMillis = System.currentTimeMillis()
                }
            } catch (e: IOException) {
                stopSelf()
                e.printStackTrace()
            }
        }
    }

    fun stopRecording() {
        Handler().post {
            try {
                mRecorder?.stop()
                mElapsedMillis = System.currentTimeMillis() - mStartingTimeMillis
                Timber.d("录音完成: $mElapsedMillis ms")
                mRecorder = null
            } catch (e: Exception) {
                stopSelf()
                Timber.d("关闭录音失败")
                e.printStackTrace()
            }
        }
    }

    fun upLoadAudio(OrderUuid: String?) {
        Timber.d("上传录音")
        try {
            ///////调用接口上传录音
            val path: String =
                getExternalFilesDir(null).toString() + IConstants.AUDIO_FOLDER + "/" + OrderUuid
            val file = File(path)
            val data = file.listFiles()
            if (data != null && data.isNotEmpty()) {
                ////上传录音前判断是否被占用
                val timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        var isAllNotUse = true
                        //判断文件是否被其他线程占用
                        for (temp in data) {
                            isAllNotUse = temp.renameTo(temp)
                        }
                        if (isAllNotUse) {
                            //取消定时操作
                            timer.cancel()
                            //执行上传音频操作
                            OssManager.uploadFile(OrderUuid, path)
                        }
                    }
                }, 200, 200)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Timber.d("是否占用错误")
            ////上传失败关闭Service
            stopSelf()
        }
    }

    private fun setFileNameAndPath(filePath: String) {
        try {
            var count = 0
            var f: File
            val audioFolder = File(getExternalFilesDir(null)?.toString() + IConstants.AUDIO_FOLDER)
            if (!audioFolder.exists()) {
                audioFolder.mkdir()
            }
            val folder =
                File(getExternalFilesDir(null)?.toString() + IConstants.AUDIO_FOLDER + "/$filePath")
            if (!folder.exists()) {
                folder.mkdir()
            }
            do {
                count++
                mFileName =
                    "${System.currentTimeMillis()}.mp3"
                mFilePath = getExternalFilesDir(null)?.absolutePath + IConstants.AUDIO_FOLDER
                mFilePath += "/$filePath/$mFileName"
                f = File(mFilePath!!)
            } while (f.exists() && !f.isDirectory)
        } catch (e: Exception) {

        }
    }
}
