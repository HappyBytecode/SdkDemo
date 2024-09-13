package anda.travel.driver.util

import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.graphics.BitmapFactory
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.Display
import android.view.WindowManager
import java.util.*

/**
 * 监听截屏事件的管理类
 */
class ScreenCaptureManager private constructor(context: Context?) {
    private val mContext: Context
    private var mListener: CaptureListener? = null
    private var mStartCaptureTime: Long = 0
    private var mInternalObserver: MediaContentObserver? = null
    private var mExternalObserver: MediaContentObserver? = null
    private val mHandler = Handler(Looper.getMainLooper())

    fun start() {
        assertInMainThread()
        mStartCaptureTime = System.currentTimeMillis()
        mInternalObserver =
            MediaContentObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI, mHandler)
        mExternalObserver =
            MediaContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mHandler)

        //Android Q(10) ContentObserver 不回调 onChange
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            // 注册内容观察者
            mContext.contentResolver.registerContentObserver(
                MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                true,
                mInternalObserver!!
            )
            mContext.contentResolver.registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true,
                mExternalObserver!!
            )
        } else {
            // 注册内容观察者
            mContext.contentResolver.registerContentObserver(
                MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                false,
                mInternalObserver!!
            )
            mContext.contentResolver.registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                false,
                mExternalObserver!!
            );
        }
    }

    fun stop() {
        assertInMainThread()
        if (mInternalObserver != null) {
            try {
                mContext.contentResolver.unregisterContentObserver(mInternalObserver!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mInternalObserver = null
        }
        if (mExternalObserver != null) {
            try {
                mContext.contentResolver.unregisterContentObserver(mExternalObserver!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mExternalObserver = null
        }
        mStartCaptureTime = 0
        mListener = null
    }

    private fun executeChange(contentUri: Uri) {
        Log.d(TAG, "executeChange. contentUri: $contentUri")
        var cursor: Cursor? = null
        try {
            cursor = mContext.contentResolver.query(
                contentUri,
                //if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.Q)PROJECTION_API_Q else PROJECTION,
                if (Build.VERSION.SDK_INT < 16) MEDIA_PROJECTIONS else MEDIA_PROJECTIONS_API_16,
                null,
                null,
                MediaStore.Images.Media.DATE_MODIFIED + " DESC"
            )

            if (cursor != null && cursor.moveToFirst()) {

                /*  val dataIndex=cursor.getColumnIndex(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                      MediaStore.Images.ImageColumns.DISPLAY_NAME else MediaStore.Images.Media.DATA)*/

                val dataIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                val dateAddedIndex =
                    cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)
                var widthIndex = -1
                var heightIndex = -1
                if (Build.VERSION.SDK_INT >= 16) {
                    widthIndex = cursor.getColumnIndex(MediaStore.Images.Media.WIDTH)
                    heightIndex = cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT)
                }
                val filePath = cursor.getString(dataIndex)
                val dateAdded = cursor.getLong(dateAddedIndex) * 1000
                val width: Int
                val height: Int
                if (widthIndex >= 0 && heightIndex >= 0) {
                    width = cursor.getInt(widthIndex)
                    height = cursor.getInt(heightIndex)
                } else {
                    val size = getImageSize(filePath)
                    width = size.x
                    height = size.y
                }
                parseFile(filePath, dateAdded, width, height)
            }
        } catch (e: Exception) {
            Log.e(TAG, "executeChange. catch: ${e.message}")
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }
    }

    private fun getImageSize(imagePath: String): Point {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imagePath, options)
        return Point(options.outWidth, options.outHeight)
    }

    private fun parseFile(
        filePath: String,
        dateAdded: Long,
        width: Int,
        height: Int
    ) {
        if (check(filePath, dateAdded, width, height)) {
            Log.d(
                TAG,
                "parseFile: path = " + filePath + "; size = " + width + " * " + height
                        + "; date = " + dateAdded
            )
            if (mListener != null && !checkCallback(filePath)) {
                mListener!!.onCapture(filePath)
            }
        } else {
            Log.e(TAG, "parseFile, it is not screen capture: path = $filePath")
        }
    }

    /**
     * 判断指定的数据行是否符合截屏条件
     */
    private fun check(
        filePath: String,
        dateAdded: Long,
        width: Int,
        height: Int
    ): Boolean { /*
         * 判断依据一: 时间判断
         */
        // 如果加入数据库的时间在开始监听之前, 或者与当前时间相差大于10秒, 则认为当前没有截屏
        Log.d(TAG, "check. dateAdded: $dateAdded")
        Log.d(TAG, "check. mStartCaptureTime: $mStartCaptureTime")
        val currentTimeMillis = System.currentTimeMillis()
        Log.d(TAG, "check. currentTimeMillis: $currentTimeMillis")
        if (dateAdded < mStartCaptureTime || currentTimeMillis - dateAdded > 10 * 1000) {
            Log.e(TAG, "check false: time")
            return false
        }
        /*
         * 判断依据二: 尺寸判断
         */if (sScreenRealSize != null) { // 如果图片尺寸超出屏幕, 则认为当前没有截屏
            if (!(width <= sScreenRealSize?.x ?: 0 && height <= sScreenRealSize?.y ?: 0
                        || height <= sScreenRealSize?.x ?: 0 && width <= sScreenRealSize?.y ?: 0)
            ) {
                Log.e(TAG, "check false: size")
                return false
            }
        }
        /*
         * 判断依据三: 路径判断
         */if (TextUtils.isEmpty(filePath)) {
            Log.e(TAG, "check false: empty")
            return false
        }
        // 判断图片路径是否含有指定的关键字之一, 如果有, 则认为当前截屏了
        for (keyWork in KEYWORDS) {
            if (filePath.toLowerCase(Locale.getDefault()).contains(keyWork)) {
                return true
            }
        }
        return false
    }

    /**
     * 判断是否已回调过, 某些手机ROM截屏一次会发出多次内容改变的通知; <br></br>
     * 删除一个图片也会发通知, 同时防止删除图片时误将上一张符合截屏规则的图片当做是当前截屏.
     */
    private fun checkCallback(imagePath: String): Boolean {
        if (sHasCallbackPaths.contains(imagePath)) {
            Log.d(
                TAG, "ScreenShot: imgPath has done"
                        + "; imagePath = " + imagePath
            )
            return true
        }
        // 大概缓存15~20条记录便可
        if (sHasCallbackPaths.size >= 20) {
            for (i in 0..4) {
                sHasCallbackPaths.removeAt(0)
            }
        }
        sHasCallbackPaths.add(imagePath)
        return false
    }

    /**
     * 获取屏幕分辨率
     */
    private val realScreenSize: Point
        private get() {
            var screenSize: Point? = null
            try {
                screenSize = Point()
                val windowManager =
                    mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val defaultDisplay = windowManager.defaultDisplay
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    defaultDisplay.getRealSize(screenSize)
                } else {
                    try {
                        val mGetRawW =
                            Display::class.java.getMethod("getRawWidth")
                        val mGetRawH =
                            Display::class.java.getMethod("getRawHeight")
                        screenSize[(mGetRawW.invoke(defaultDisplay) as Int)] =
                            (mGetRawH.invoke(defaultDisplay) as Int)
                    } catch (e: Exception) {
                        screenSize[defaultDisplay.width] = defaultDisplay.height
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return screenSize!!
        }

    /**
     * 设置截屏监听器
     */
    fun setListener(listener: CaptureListener?) {
        mListener = listener
    }

    interface CaptureListener {
        fun onCapture(imagePath: String?)
    }

    /**
     * 媒体内容观察者(观察媒体数据库的改变)
     */
    private inner class MediaContentObserver(
        private val mContentUri: Uri,
        handler: Handler?
    ) : ContentObserver(handler) {
        override fun onChange(selfChange: Boolean) {
            executeChange(mContentUri)
            super.onChange(selfChange)
        }
    }

    companion object {
        private const val TAG = "ScreenCaptureManager"
        /* */
        /**
         * 读取媒体数据库时需要读取的列, 其中 WIDTH 和 HEIGHT 字段在 API 16 以后才有
         *//*
        private val PROJECTION = arrayOf(
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT
        )*/


        /*private val PROJECTION_API_Q = arrayOf(
                MediaStore.Images.ImageColumns.RELATIVE_PATH,
                MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.WIDTH,
                MediaStore.Images.ImageColumns.HEIGHT
        )*/

        /**
         * 读取媒体数据库时需要读取的列
         */
        private val MEDIA_PROJECTIONS = arrayOf(
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.DATE_ADDED
        )

        /**
         * 读取媒体数据库时需要读取的列, 其中 WIDTH 和 HEIGHT 字段在 API 16 以后才有
         */
        private val MEDIA_PROJECTIONS_API_16 = arrayOf(
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.DATE_ADDED,
            MediaStore.Images.ImageColumns.WIDTH,
            MediaStore.Images.ImageColumns.HEIGHT
        )

        /**
         * 截屏依据中的路径判断关键字
         */
        private val KEYWORDS = arrayOf(
            "screenshot", "screen_shot", "screen-shot", "screen shot",
            "screencapture", "screen_capture", "screen-capture", "screen capture",
            "screencap", "screen_cap", "screen-cap", "screen cap", "截屏"
        )
        private var sScreenRealSize: Point? = null

        /**
         * 已回调过的路径
         */
        private val sHasCallbackPaths: MutableList<String> =
            ArrayList()

        fun newInstance(context: Context?): ScreenCaptureManager {
            assertInMainThread()
            return ScreenCaptureManager(context)
        }

        private fun assertInMainThread() {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                val elements =
                    Thread.currentThread().stackTrace
                var methodMsg: String? = null
                if (elements.size >= 4) {
                    methodMsg = elements[3].toString()
                }
                throw IllegalStateException("Call the method must be in main thread: $methodMsg")
            }
        }
    }

    init {
        requireNotNull(context) { "The context must not be null." }
        mContext = context
        // 获取屏幕真实的分辨率
        if (sScreenRealSize == null) {
            sScreenRealSize = realScreenSize
            if (sScreenRealSize != null) {
                Log.d(
                    TAG,
                    "Screen Real Size: " + sScreenRealSize?.x + " * " + sScreenRealSize?.y
                )
            } else {
                Log.w(
                    TAG,
                    "Get screen real size failed."
                )
            }
        }
    }
}