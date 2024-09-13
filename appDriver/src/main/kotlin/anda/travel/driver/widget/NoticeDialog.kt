package anda.travel.driver.widget

import anda.travel.driver.R
import anda.travel.driver.baselibrary.view.update.utils.ScreenUtil
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlin.math.abs

/**
 * 提醒弹框
 */
class NoticeDialog(context: Context) : Dialog(context, R.style.alert_dialog) {

    private var mCancelBtn: TextView
    private var mConfirmBtn: TextView
    private var mTitleTv: TextView
    private var mContentTv: TextView
    private var mImageIv: ImageView

    private var mCancelListener: OnClickListener? = null
    private var mConfirmListener: OnClickListener? = null
    private var mBtnClickStamp //记录按键点击的时间戳
            : Long = 0

    fun isBtnBuffering(): Boolean {
        val duration: Long = System.currentTimeMillis() - mBtnClickStamp
        mBtnClickStamp = System.currentTimeMillis() //记录点击时间
        return abs(duration) <= 500L
    }

    init {
        setContentView(R.layout.hxyc_dialog_notice)
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        val dialogWindow = this.window
        val lp = dialogWindow!!.attributes
        lp.width = (ScreenUtil.getWith(context) * 0.8f).toInt()
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        dialogWindow!!.attributes = lp

        mCancelBtn = findViewById(R.id.notice_cancel)
        mConfirmBtn = findViewById(R.id.notice_confirm)
        mTitleTv = findViewById(R.id.notice_title)
        mContentTv = findViewById(R.id.notice_content)
        mImageIv = findViewById(R.id.notice_img)
        mCancelBtn.setOnClickListener {
            if (isBtnBuffering()) return@setOnClickListener  //避免快速点击
            if (mCancelListener != null) {
                mCancelListener!!.onClick()
            }
            dismiss()
        }

        mConfirmBtn.setOnClickListener {
            if (isBtnBuffering()) return@setOnClickListener  //避免快速点击
            if (mConfirmBtn != null) {
                mConfirmListener!!.onClick()
            }
            dismiss()
        }
    }

    fun setConfirmListener(onClickListener: OnClickListener): NoticeDialog {
        mConfirmListener = onClickListener
        return this
    }

    fun setCancelListener(onClickListener: OnClickListener): NoticeDialog {
        mCancelListener = onClickListener
        return this
    }

    fun setNoticeTitle(title: String): NoticeDialog {
        mTitleTv.text = title
        return this
    }

    fun setNoticeContent(content: String): NoticeDialog {
        mContentTv.text = content
        return this
    }

    fun setNoticeContentColor(res: Int): NoticeDialog {
        val color = ContextCompat.getColor(context, res)
        mContentTv.setTextColor(color)
        return this
    }

    fun setImageRes(res: Int): NoticeDialog {
        mImageIv.setImageResource(res)
        return this
    }

    fun setCancelText(cancelContent: String): NoticeDialog {
        mCancelBtn.text = cancelContent
        return this
    }

    fun setConfirmText(confirmContent: String): NoticeDialog {
        mConfirmBtn.text = confirmContent
        return this
    }

    interface OnClickListener {
        fun onClick()
    }
}
