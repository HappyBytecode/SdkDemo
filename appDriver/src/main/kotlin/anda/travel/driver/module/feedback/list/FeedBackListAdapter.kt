package anda.travel.driver.module.feedback.list

import anda.travel.driver.baselibrary.adapter.internal.SuperViewHolder
import anda.travel.driver.R
import anda.travel.driver.common.BaseActivity
import anda.travel.driver.data.entity.FeedBackEntity
import anda.travel.driver.pictureselect.GlideEngine
import anda.travel.driver.widget.layout.BaseLinearLayout
import anda.travel.driver.baselibrary.utils.DisplayUtil
import anda.travel.driver.baselibrary.view.refreshview.RefreshAdapter
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.style.PictureSelectorStyle
import java.util.ArrayList
import kotlin.math.roundToInt

class FeedBackListAdapter(context: Context?) : RefreshAdapter<FeedBackEntity>(context, listOf(), R.layout.hxyc_item_feedback_list) {

    override fun onBind(holder: SuperViewHolder, viewType: Int, position: Int, item: FeedBackEntity) {
        holder.run {
            val itemView = this.getView<BaseLinearLayout>(R.id.layout_feedback_item)
            itemView?.radius = 10
            itemView?.shadowColor = R.color.item_gray_primary
            itemView?.shadowElevation = 16
            val photos = this.getView<LinearLayout>(R.id.layout_photos)
            val photoWidth = ((DisplayUtil.getScreenW(context) - DisplayUtil.dp2px(mContext, 109f)) / 4f).roundToInt()

            val margin: Int = DisplayUtil.dp2px(mContext, 16.33f)
            val lastParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(photoWidth, DisplayUtil.dp2px(mContext, 80 * 1f))
            val btnRightParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(photoWidth, DisplayUtil.dp2px(mContext, 80 * 1f))
            btnRightParams.rightMargin = margin
            if (!item.paths.isNullOrEmpty()) {
                photos.visibility = View.VISIBLE
                photos?.removeAllViews()
                val list = mutableListOf<LocalMedia>()
                for (i in item.paths.indices) {
                    val img = ImageView(context)
                    img.scaleType = ImageView.ScaleType.FIT_XY
                    Glide.with(mContext)
                            .load(item.paths?.get(i)).placeholder(R.drawable.ic_feedback_default)
                            .into(img)
                    val localMedia = LocalMedia()
                    localMedia.path = item.paths?.get(i)
                    list.add(localMedia)
                    if (i == 3) {
                        img.layoutParams = lastParams
                    } else {
                        img.layoutParams = btnRightParams
                    }
                    img.setOnClickListener {
                        // 预览图片、视频、音频
                        PictureSelector.create(context)
                                .openPreview()
                                .setImageEngine(GlideEngine.createGlideEngine())
                                .setSelectorUIStyle(PictureSelectorStyle())
                                .isPreviewFullScreenMode(false)
                                .startActivityPreview(i, false, list as ArrayList<LocalMedia>?)

                    }
                    photos?.addView(img)
                }
            } else {
                photos.visibility = View.GONE
            }
            this.setText(R.id.tv_status, if (item.status == 1) "" else "已回复")
            if (!item.content.isNullOrEmpty()) {
                this.setText(R.id.tv_feedback_content, item.content)
                this.getView<TextView>(R.id.tv_feedback_content)!!.visibility = View.VISIBLE
            } else {
                this.getView<TextView>(R.id.tv_feedback_content)!!.visibility = View.GONE
            }
            if (!item.createTime.isNullOrEmpty()) {
                this.setText(R.id.tv_time, item.createTime)
                this.getView<TextView>(R.id.tv_time)!!.visibility = View.VISIBLE
            } else {
                this.getView<TextView>(R.id.tv_time)!!.visibility = View.GONE
            }
        }
    }
}
