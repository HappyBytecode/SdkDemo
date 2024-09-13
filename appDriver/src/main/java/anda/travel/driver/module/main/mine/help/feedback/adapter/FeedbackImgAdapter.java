package anda.travel.driver.module.main.mine.help.feedback.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.luck.picture.lib.config.PictureMimeType;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.adapter.SuperAdapter;
import anda.travel.driver.baselibrary.adapter.internal.SuperViewHolder;
import anda.travel.driver.module.vo.FeedbackImgVo;

/**
 * 意见反馈图片选择adapter
 */
public class FeedbackImgAdapter extends SuperAdapter<FeedbackImgVo> {

    private OnDeleteClickListener mDeleteListener;

    public FeedbackImgAdapter(Context context) {
        super(context, null, R.layout.hxyc_item_feeedback_img);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int pos);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener clickListener) {
        mDeleteListener = clickListener;
    }

    @Override
    public void onBind(SuperViewHolder holder, int viewType, int position, FeedbackImgVo item) {
        if (item.isShowAddImg()) {
            holder.setVisibility(R.id.layout_add, View.VISIBLE);
            holder.setVisibility(R.id.img_delete, View.INVISIBLE);
            holder.setVisibility(R.id.img_feedback, View.GONE);
        } else {
            holder.setVisibility(R.id.layout_add, View.GONE);
            holder.setVisibility(R.id.img_feedback, View.VISIBLE);
            holder.setVisibility(R.id.img_delete, View.VISIBLE);
            holder.setOnClickListener(R.id.img_delete, v -> mDeleteListener.onDeleteClick(position));
            Glide.with(holder.itemView.getContext())
                    .load(PictureMimeType.isContent(item.getPath()) ? Uri.parse(item.getPath())
                            : item.getPath())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into((ImageView) holder.getView(R.id.img_feedback));
        }
    }
}
