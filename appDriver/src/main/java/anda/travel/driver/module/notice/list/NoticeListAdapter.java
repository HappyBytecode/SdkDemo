package anda.travel.driver.module.notice.list;

import android.content.Context;

import java.util.ArrayList;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.adapter.SuperAdapter;
import anda.travel.driver.baselibrary.adapter.internal.SuperViewHolder;
import anda.travel.driver.data.entity.NoticeEntity;
import anda.travel.driver.util.TimeUtils;

class NoticeListAdapter extends SuperAdapter<NoticeEntity> {

    public NoticeListAdapter(Context context) {
        super(context, new ArrayList<>(), R.layout.hxyc_item_notice);
    }

    @Override
    public void onBind(SuperViewHolder holder, int viewType, int position, NoticeEntity item) {
        holder.setText(R.id.tv_time, TimeUtils.formatTime(item.getCreateTime(), "yyyy-MM-dd HH:mm"));
        holder.setText(R.id.tv_content, item.getTitle());
    }
}
