package anda.travel.driver.module.feedback.details

import anda.travel.driver.R
import anda.travel.driver.baselibrary.utils.DisplayUtil
import anda.travel.driver.common.AppConfig
import anda.travel.driver.common.BaseActivity
import anda.travel.driver.config.IConstants
import anda.travel.driver.data.entity.FeedBackEntity
import anda.travel.driver.databinding.HxycActivityFeedbackDetailsBinding
import anda.travel.driver.module.feedback.details.di.DaggerFeedBackDetailsComponent
import anda.travel.driver.module.feedback.details.di.FeedBackDetailsModule
import anda.travel.driver.pictureselect.GlideEngine
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureSelectionConfig.imageEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.style.PictureSelectorStyle
import java.util.ArrayList
import kotlin.math.roundToInt

class FeedBackDetailsActivity : BaseActivity(), FeedBackDetailsContract.View {

    private lateinit var binding: HxycActivityFeedbackDetailsBinding
    private var mFeedBackEntity: FeedBackEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerFeedBackDetailsComponent.builder().appComponent(appComponent)
                .feedBackDetailsModule(FeedBackDetailsModule(this))
                .build().inject(this)
        binding = HxycActivityFeedbackDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mFeedBackEntity = intent.getParcelableExtra(IConstants.FEEDBACK)
        setData()
        AppConfig.setFloatView(false)
    }

    override fun setData() {
        val layoutFeedBack = binding.layoutFeedback
        layoutFeedBack.radius = 10
        layoutFeedBack.shadowColor = R.color.item_gray_primary
        layoutFeedBack.shadowElevation = 16

        val photos = binding.layoutPhotos
        val photoWidth =
                ((DisplayUtil.getScreenW(this) - DisplayUtil.dp2px(this, 109f)) / 4f).roundToInt()

        val margin: Int = DisplayUtil.dp2px(this, 16.33f)
        val lastParams: LinearLayout.LayoutParams =
                LinearLayout.LayoutParams(photoWidth, DisplayUtil.dp2px(this, 80 * 1f))
        val btnRightParams: LinearLayout.LayoutParams =
                LinearLayout.LayoutParams(photoWidth, DisplayUtil.dp2px(this, 80 * 1f))
        btnRightParams.rightMargin = margin
        if (mFeedBackEntity != null) {
            if (!mFeedBackEntity!!.paths.isNullOrEmpty()) {
                photos.removeAllViews()
                val list = mutableListOf<LocalMedia>()
                for (i in mFeedBackEntity!!.paths.indices) {
                    val img = ImageView(this)
                    img.scaleType = ImageView.ScaleType.FIT_XY
                    Glide.with(this)
                            .load(mFeedBackEntity!!.paths?.get(i))
                            .placeholder(R.drawable.ic_feedback_default)
                            .into(img)
                    val localMedia = LocalMedia()
                    localMedia.path = mFeedBackEntity!!.paths?.get(i)
                    list.add(localMedia)
                    if (i == 3) {
                        img.layoutParams = lastParams
                    } else {
                        img.layoutParams = btnRightParams
                    }
                    img.setOnClickListener {

                        // 预览图片、视频、音频
                        PictureSelector.create(this)
                                .openPreview()
                                .setImageEngine(GlideEngine.createGlideEngine())
                                .setSelectorUIStyle(PictureSelectorStyle())
                                .isPreviewFullScreenMode(false)
                                .startActivityPreview(i, false, list as ArrayList<LocalMedia>?)
                    }
                    photos.addView(img)
                }
            } else {
                photos.visibility = View.GONE
            }

            binding.tvStatus.text = if (mFeedBackEntity!!.status == 1) "" else "已回复"
            if (!mFeedBackEntity!!.content.isNullOrEmpty()) {
                binding.tvFeedbackContent.text = mFeedBackEntity!!.content
            } else {
                binding.tvFeedbackContent.visibility = View.GONE
            }
            if (!mFeedBackEntity!!.createTime.isNullOrEmpty()) {
                binding.tvTime.text = mFeedBackEntity!!.createTime
            }
            if (!mFeedBackEntity!!.result.isNullOrEmpty()) {
                binding.tvReply.text = mFeedBackEntity!!.result
            } else {
                binding.tvReply.text = "您的反馈我们已收到，请您耐心等待。"
            }
        }
    }

    override fun isActive(): Boolean {
        return true
    }
}