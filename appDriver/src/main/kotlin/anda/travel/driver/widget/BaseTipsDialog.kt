package anda.travel.driver.widget

import anda.travel.driver.R
import anda.travel.driver.baselibrary.view.update.utils.ScreenUtil
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlin.math.abs

/**
 * 单个按钮的弹框
 */
class BaseTipsDialog(context: Context) : Dialog(context, R.style.alert_dialog) {

    private var mConfirmBtn: TextView
    private var mTitleTv: TextView
    private var mContentTv: TextView
    private var mImageIv: ImageView

    private var mConfirmListener: OnClickListener? = null
    private var mBtnClickStamp //记录按键点击的时间戳
            : Long = 0

    fun isBtnBuffering(): Boolean {
        val duration: Long = System.currentTimeMillis() - mBtnClickStamp
        mBtnClickStamp = System.currentTimeMillis() //记录点击时间
        return abs(duration) <= 500L
    }

    init {
        setContentView(R.layout.hxyc_dialog_base_tips)
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        val dialogWindow = this.window
        val lp = dialogWindow!!.attributes
        lp.width = (ScreenUtil.getWith(context) * 0.8f).toInt()
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        dialogWindow!!.attributes = lp

        mConfirmBtn = findViewById(R.id.tips_confirm)
        mTitleTv = findViewById(R.id.tips_title)
        mContentTv = findViewById(R.id.tips_content)
        mImageIv = findViewById(R.id.tips_img)
        mConfirmBtn.setOnClickListener {
            if (isBtnBuffering()) return@setOnClickListener  //避免快速点击
            if (mConfirmListener != null) {
                mConfirmListener!!.onClick()
            }
            dismiss()
        }
    }

    fun setConfirmListener(onClickListener: OnClickListener): BaseTipsDialog {
        mConfirmListener = onClickListener
        return this
    }

    fun setTipsTitle(title: String): BaseTipsDialog {
        mTitleTv.visibility = View.VISIBLE
        mTitleTv.text = title
        return this
    }

    fun setTipsContent(content: String): BaseTipsDialog {
        mContentTv.visibility = View.VISIBLE
        mContentTv.text = content
        return this
    }

    fun setTipsContentColor(res: Int): BaseTipsDialog {
        val color = ContextCompat.getColor(context, res)
        mContentTv.setTextColor(color)
        return this
    }

    fun setImageRes(res: Int): BaseTipsDialog {
        mImageIv.setImageResource(res)
        return this
    }

    fun setConfirmText(confirmContent: String): BaseTipsDialog {
        mConfirmBtn.text = confirmContent
        return this
    }

    interface OnClickListener {
        fun onClick()
    }
}
