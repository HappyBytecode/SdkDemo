package anda.travel.driver.widget

import anda.travel.driver.R
import anda.travel.driver.util.Navigate
import anda.travel.driver.util.SysConfigUtils
import anda.travel.driver.baselibrary.view.update.utils.ScreenUtil
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import kotlin.math.abs

/**
 * 单个按钮的弹框
 */
class HelpDialog(context: Context) : Dialog(context, R.style.alert_dialog) {

    private var mFeedBackTv: TextView
    private var mServiceTv: TextView
    private var mCloseIv: ImageView

    private var mBtnClickStamp //记录按键点击的时间戳
            : Long = 0

    fun isBtnBuffering(): Boolean {
        val duration: Long = System.currentTimeMillis() - mBtnClickStamp
        mBtnClickStamp = System.currentTimeMillis() //记录点击时间
        return abs(duration) <= 500L
    }

    init {
        setContentView(R.layout.hxyc_dialog_help)
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        val dialogWindow = this.window
        val lp = dialogWindow!!.attributes
        lp.width = (ScreenUtil.getWith(context) * 0.84f).toInt()
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        dialogWindow!!.attributes = lp
        dialogWindow.setDimAmount(0.7f)

        mFeedBackTv = findViewById(R.id.tv_feedback)
        mServiceTv = findViewById(R.id.tv_call_service)
        mCloseIv = findViewById(R.id.iv_close)

        mCloseIv.setOnClickListener {
            dismiss()
        }

        mFeedBackTv.setOnClickListener {
            if (isBtnBuffering()) return@setOnClickListener  //避免快速点击
            dismiss()
            Navigate.openFeedback(context)
        }

        mServiceTv.setOnClickListener {
            if (isBtnBuffering()) return@setOnClickListener  //避免快速点击
            dismiss()
            SysConfigUtils.get().dialServerPhone(context)
        }
    }
}
