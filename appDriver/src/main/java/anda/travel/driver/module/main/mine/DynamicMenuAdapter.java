package anda.travel.driver.module.main.mine;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import anda.travel.driver.baselibrary.adapter.SuperAdapter;
import anda.travel.driver.baselibrary.adapter.internal.SuperViewHolder;
import anda.travel.driver.R;
import anda.travel.driver.data.entity.UserCenterMenuEntity;

class DynamicMenuAdapter extends SuperAdapter<UserCenterMenuEntity.MenusBean> {
    Context mContext;

    public DynamicMenuAdapter(Context context) {
        super(context, new ArrayList<>(), R.layout.hxyc_item_dynamic_menu);
        mContext = context;
    }

    @Override
    public void onBind(SuperViewHolder holder, int viewType, int position, UserCenterMenuEntity.MenusBean item) {
        ImageView imageView = holder.getView(R.id.menu_ico);
        Glide.with(mContext).load(item.getIcon()).into(imageView);
        holder.setText(R.id.menu_content, item.getName());
    }
}
