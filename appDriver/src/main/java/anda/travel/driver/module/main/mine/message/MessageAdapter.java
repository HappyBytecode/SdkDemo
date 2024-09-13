package anda.travel.driver.module.main.mine.message;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Date;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.adapter.IMulItemViewType;
import anda.travel.driver.baselibrary.adapter.internal.SuperViewHolder;
import anda.travel.driver.baselibrary.utils.DateUtil;
import anda.travel.driver.baselibrary.utils.GlideRoundTransform;
import anda.travel.driver.baselibrary.utils.TypeUtil;
import anda.travel.driver.baselibrary.view.refreshview.RefreshAdapter;
import anda.travel.driver.data.entity.HxMessageEntity;
import anda.travel.driver.widget.layout.BaseLinearLayout;

class MessageAdapter extends RefreshAdapter<HxMessageEntity> {

    public static final int MESSAGE_NOPIC = 1;
    public static final int MESSAGE_PIC = 2;
    private final Context mContext;

    public MessageAdapter(Context context) {
        super(context, new ArrayList<>(), null);
        mContext = context;
    }

    @Override
    public void onBind(SuperViewHolder holder, int viewType, int position, HxMessageEntity item) {
        String curTime = item.getPushTime() == null || item.getPushTime() <= 0
                ? ""
                : DateUtil.formatDate(new Date(item.getPushTime()), "MM/dd HH:mm");
        String title = "";
        if (item.getType() == 1) {
            title = "系统消息";
        } else {
            if (!TextUtils.isEmpty(item.getTitle()))
                title = item.getTitle();
        }

        switch (viewType) {
            case MESSAGE_NOPIC:
                holder.setText(R.id.tv_date, curTime)
                        .setText(R.id.tv_title, title)
                        .setText(R.id.tv_content, TypeUtil.getValue(item.getContent()));
                break;
            case MESSAGE_PIC:
                holder.setText(R.id.tv_date, curTime)
                        .setText(R.id.tv_title, title)
                        .setText(R.id.tv_content, TypeUtil.getValue(item.getContent()));
                Glide.with(mContext).load(item.getPic())
                        .placeholder(R.drawable.msg_default)
                        .transform(new GlideRoundTransform(5))
                        .into((ImageView) holder.getView(R.id.iv_pic));
                break;
            default:
                break;
        }

        ((BaseLinearLayout) holder.getView(R.id.layout_item)).setRadiusAndShadow(20, 16);
        if (item.getStatus() == 1) {
            //未读
            holder.getView(R.id.tv_red_point).setVisibility(View.VISIBLE);
        } else if (item.getStatus() == 2) {
            //已读
            holder.getView(R.id.tv_red_point).setVisibility(View.GONE);
        }
    }

    @Override
    protected IMulItemViewType<HxMessageEntity> offerMultiItemViewType() {
        return new IMulItemViewType<HxMessageEntity>() {
            @Override
            public int getItemViewType(int position, HxMessageEntity item) {
                if (TextUtils.isEmpty(item.getPic())) {
                    return MESSAGE_NOPIC;
                } else {
                    return MESSAGE_PIC;
                }
            }

            @Override
            public int getLayoutId(int viewType) {
                if (viewType == MESSAGE_NOPIC) {
                    return R.layout.hxyc_item_message;
                }
                return R.layout.hxyc_item_message_pic;
            }
        };
    }
}
