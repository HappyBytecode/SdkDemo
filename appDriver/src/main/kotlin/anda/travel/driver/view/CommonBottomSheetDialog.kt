package anda.travel.driver.view

import anda.travel.driver.R
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.core.app.ActivityCompat

/**
 * R.layout.dialog_bottom_sheet_two_btn
 * 基础弹窗
 */
class CommonBottomSheetDialog(context: Context) : BaseBottomSheetDialog(context) {
    interface ClickListener {
        fun onClick(dialog: CommonBottomSheetDialog)
    }

    companion object {
        const val INVALIDATE_RES = -1
    }

    private val mCancelBtn: TextView? by lazy(LazyThreadSafetyMode.NONE) {
        this.findViewById(R.id.cancelBtn)
    }
    private val mTitleTv: TextView? by lazy(LazyThreadSafetyMode.NONE) {
        this.findViewById(R.id.titleTv)
    }
    private val mSubTitleTv: TextView? by lazy(LazyThreadSafetyMode.NONE) {
        this.findViewById(R.id.subTitleTv)
    }
    private val mConfirmBtn: TextView? by lazy(LazyThreadSafetyMode.NONE) {
        this.findViewById(R.id.confirmBtn)
    }
    private val mIv: ImageView? by lazy {
        findViewById(R.id.iv)
    }

    private fun setContentViewId(viewId: Int): CommonBottomSheetDialog {
        if (viewId == INVALIDATE_RES) {
            setContentView(R.layout.hxyc_dialog_bottom_sheet_two_btn)
        } else {
            setContentView(viewId)
        }
        window?.apply {
            attributes?.dimAmount = 0f
            setBackgroundDrawable(
                ColorDrawable(
                    ActivityCompat.getColor(
                        context,
                        R.color.dialog_bg
                    )
                )
            )
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }
        dismissWithAnimation = true
        return this
    }

    private fun setCancelAble(cancelAble: Boolean): CommonBottomSheetDialog {
        setCancelable(cancelAble)
        return this
    }

    private fun setConfirmText(text: String?): CommonBottomSheetDialog {
        if (text == null) return this
        mConfirmBtn?.visibility = View.VISIBLE
        mConfirmBtn?.text = text
        return this
    }

    fun setCancelText(text: String?): CommonBottomSheetDialog {
        if (text == null) return this
        mCancelBtn?.visibility = View.VISIBLE
        mCancelBtn?.text = text
        return this
    }

    private fun setTitleText(text: String?): CommonBottomSheetDialog {
        if (text == null) return this
        mTitleTv?.visibility = View.VISIBLE
        mTitleTv?.text = text
        return this
    }

    private fun setSubTitleText(text: String?): CommonBottomSheetDialog {
        if (text == null) return this
        mSubTitleTv?.visibility = View.VISIBLE
        mSubTitleTv?.text = text
        return this
    }

    private fun setConfirmListener(listener: ClickListener?): CommonBottomSheetDialog {
        if (listener == null) return this
        mConfirmBtn?.visibility = View.VISIBLE
        mConfirmBtn?.setOnClickListener {
            listener.onClick(this)
        }
        return this
    }

    private fun setCancelListener(listener: ClickListener?): CommonBottomSheetDialog {
        if (listener == null) return this
        mCancelBtn?.visibility = View.VISIBLE
        mCancelBtn?.setOnClickListener { listener.onClick(this) }
        return this
    }

    private fun setText(@IdRes idRes: Int, str: CharSequence): CommonBottomSheetDialog {
        if (idRes == -1) return this
        findViewById<TextView>(idRes)?.text = str
        return this
    }

    private fun setTextWhitTypeface(
        @IdRes idRes: Int,
        tf: Typeface,
        str: String
    ): CommonBottomSheetDialog {
        if (idRes == -1) return this
        findViewById<TextView>(idRes)?.apply {
            typeface = tf
            text = str
        }
        return this
    }

    private fun setImageSrc(@DrawableRes res: Int?): CommonBottomSheetDialog {
        if (res == null) return this
        mIv?.visibility = View.VISIBLE
        mIv?.setImageResource(res)
        return this
    }

    fun showDlg(): CommonBottomSheetDialog {
        show()
        return this
    }

    class Builder(context: Context, @LayoutRes contentLayoutId: Int = INVALIDATE_RES) {
        private lateinit var mDialog: CommonBottomSheetDialog

        constructor(context: Context) : this(context, INVALIDATE_RES)

        private val mContext = context

        private val mParams = DialogParams(contentLayoutId)

        fun setCancelAble(cancelAble: Boolean): Builder {
            mParams.mCancelAble = cancelAble
            return this
        }

        fun setConfirmText(text: String): Builder {
            mParams.mConfirmText = text
            return this
        }

        fun setCancelText(text: String): Builder {
            mParams.mCancelText = text
            return this
        }

        fun setTitleText(text: String): Builder {
            mParams.mTitleText = text
            return this
        }

        fun setSubTitleText(text: String): Builder {
            mParams.mSubTitleText = text
            return this
        }

        fun setConfirmListener(listener: ClickListener): Builder {
            mParams.mConfirmListener = listener
            return this
        }

        fun setCancelListener(listener: ClickListener): Builder {
            mParams.mCancelListener = listener
            return this
        }

        fun setText(@IdRes idRes: Int, str: CharSequence): Builder {
            mParams.mTextMap[idRes] = str
            return this
        }

        //showDlg()之前必须调用来初始化Dialog参数
        fun create(): CommonBottomSheetDialog {
            mDialog = CommonBottomSheetDialog(mContext)
                .setContentViewId(mParams.mContentViewId)
                .setTitleText(mParams.mTitleText)
                .setSubTitleText(mParams.mSubTitleText)
                .setConfirmListener(mParams.mConfirmListener)
                .setCancelListener(mParams.mCancelListener)
                .setConfirmText(mParams.mConfirmText)
                .setCancelText(mParams.mCancelText)
                .setCancelAble(mParams.mCancelAble)
                .setImageSrc(mParams.mImageSrc)
            if (mParams.mTextMap.size > 0) {
                mParams.mTextMap.forEach {
                    if (it.key != -1) {
                        mDialog.setText(it.key, it.value)
                    }
                }
            }
            if (mParams.mTextTypefaceMap.size > 0) {
                mParams.mTextTypefaceMap.forEach {
                    if (it.key != -1) {
                        mDialog.setTextWhitTypeface(it.key, it.value.tf, it.value.str)
                    }
                }
            }
            return mDialog
        }

        fun show(): CommonBottomSheetDialog {
            create().showDlg()
            return mDialog
        }
    }

    private class DialogParams(@LayoutRes contentLayoutId: Int) {
        var mCancelListener: ClickListener? = null
        var mConfirmListener: ClickListener? = null
        var mSubTitleText: String? = null
        var mCancelAble: Boolean = true
        var mContentViewId: Int = contentLayoutId
        var mConfirmText: String? = null
        var mCancelText: String? = null
        var mTitleText: String? = null
        var mImageSrc: Int? = null
        var mTextMap: HashMap<Int, CharSequence> = HashMap()
        var mTextTypefaceMap: HashMap<Int, TypefaceString> = HashMap()
    }

    data class TypefaceString(var id: Int, var tf: Typeface, var str: String)
}