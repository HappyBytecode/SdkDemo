package anda.travel.driver.module.main.home;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.adapter.SuperAdapter;
import anda.travel.driver.baselibrary.adapter.internal.SuperViewHolder;
import anda.travel.driver.baselibrary.utils.DateUtil;
import anda.travel.driver.baselibrary.utils.TypeUtil;
import anda.travel.driver.data.entity.HxMessageEntity;

/**
 * 功能描述：
 */
class HomeAdapter extends SuperAdapter<HxMessageEntity> {

    public HomeAdapter(Context context) {
        super(context, new ArrayList<>(), R.layout.hxyc_item_home_list);
    }

    @Override
    public void onBind(SuperViewHolder holder, int viewType, int position, HxMessageEntity item) {
        String curTime = item.getPushTime() == null || item.getPushTime() <= 0
                ? ""
                : DateUtil.formatDate(new Date(item.getPushTime()), "MM/dd HH:mm");
        holder.setText(R.id.tv_time, curTime)
                .setText(R.id.tv_type, TypeUtil.getValue(item.getTypeStr()))
                .setText(R.id.tv_content, TypeUtil.getValue(item.getContent()));
    }
}
