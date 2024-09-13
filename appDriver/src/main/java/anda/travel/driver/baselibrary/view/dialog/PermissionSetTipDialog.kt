package anda.travel.driver.baselibrary.view.dialog

import anda.travel.driver.R
import anda.travel.driver.baselibrary.view.update.utils.ScreenUtil
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import kotlin.math.abs

/**
 * 单个按钮的弹框
 */
class PermissionSetTipDialog(context: Context) : Dialog(context, R.style.alert_dialog) {

    private var mConfirmBtn: TextView
    private var mTitleTv: TextView
    private var mSmallTitle1Tv: TextView
    private var mSmallTitle2Tv: TextView
    private var mSmallTitle3Tv: TextView
    private var mSmallTitle4Tv: TextView
    private var mSmallContent1: TextView
    private var mSmallContent2: TextView
    private var mSmallContent3: TextView
    private var mSmallContent4: TextView

    private var mConfirmListener: OnClickListener? = null
    private var mBtnClickStamp //记录按键点击的时间戳
            : Long = 0

    fun isBtnBuffering(): Boolean {
        val duration: Long = System.currentTimeMillis() - mBtnClickStamp
        mBtnClickStamp = System.currentTimeMillis() //记录点击时间
        return abs(duration) <= 500L
    }

    init {
        setContentView(R.layout.hxyc_dialog_permission_set_tips)
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
        mSmallTitle1Tv = findViewById(R.id.tips_small_title1)
        mSmallTitle2Tv = findViewById(R.id.tips_small_title2)
        mSmallTitle3Tv = findViewById(R.id.tips_small_title3)
        mSmallTitle4Tv = findViewById(R.id.tips_small_title4)
        mSmallContent1 = findViewById(R.id.tips_small_content1)
        mSmallContent2 = findViewById(R.id.tips_small_content2)
        mSmallContent3 = findViewById(R.id.tips_small_content3)
        mSmallContent4 = findViewById(R.id.tips_small_content4)
        mConfirmBtn.setOnClickListener {
            if (isBtnBuffering()) return@setOnClickListener  //避免快速点击
            if (mConfirmListener != null) {
                mConfirmListener!!.onClick()
            }
            dismiss()
        }
    }

    fun setConfirmListener(onClickListener: OnClickListener): PermissionSetTipDialog {
        mConfirmListener = onClickListener
        return this
    }

    fun setTipsSmallTitleAndContent1(title: String, content: String): PermissionSetTipDialog {
        mSmallTitle1Tv.visibility = View.VISIBLE
        mSmallTitle1Tv.text = title
        mSmallContent1.visibility = View.VISIBLE
        mSmallContent1.text = content
        return this
    }

    fun setTipsSmallTitleAndContent2(title: String, content: String): PermissionSetTipDialog {
        mSmallTitle2Tv.visibility = View.VISIBLE
        mSmallTitle2Tv.text = title
        mSmallContent2.visibility = View.VISIBLE
        mSmallContent2.text = content
        return this
    }

    fun setTipsSmallTitleAndContent3(title: String, content: String): PermissionSetTipDialog {
        mSmallTitle3Tv.visibility = View.VISIBLE
        mSmallTitle3Tv.text = title
        mSmallContent3.visibility = View.VISIBLE
        mSmallContent3.text = content
        return this
    }

    fun setTipsSmallTitleAndContent4(title: String, content: String): PermissionSetTipDialog {
        mSmallTitle4Tv.visibility = View.VISIBLE
        mSmallTitle4Tv.text = title
        mSmallContent4.visibility = View.VISIBLE
        mSmallContent4.text = content
        return this
    }

    fun setConfirmText(confirmContent: String): PermissionSetTipDialog {
        mConfirmBtn.text = confirmContent
        return this
    }

    interface OnClickListener {
        fun onClick()
    }
}