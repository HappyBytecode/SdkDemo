package anda.travel.driver.module.main.home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.adapter.SuperAdapter;
import anda.travel.driver.baselibrary.adapter.internal.SuperViewHolder;
import anda.travel.driver.data.entity.DynamicIconEntity;

public class DynamicIconAdapter extends SuperAdapter<DynamicIconEntity.IconBean> {
    Context mContext;

    public DynamicIconAdapter(Context context) {
        super(context, new ArrayList<>(), R.layout.hxyc_item_dynamic_icon);
        mContext = context;
    }

    @Override
    public void onBind(SuperViewHolder holder, int viewType, int position, DynamicIconEntity.IconBean item) {
        ImageView imageView = holder.getView(R.id.iv_icon);
        Glide.with(mContext).load(item.getImgUrl()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                imageView.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                imageView.setVisibility(View.VISIBLE);
                return false;
            }
        }).into(imageView);
        holder.setText(R.id.tv_text, item.getName());
        try {
            if (!TextUtils.isEmpty(item.getBackdropColour())) {
                holder.setTextColor(R.id.tv_text, Color.parseColor(item.getBackdropColour()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
