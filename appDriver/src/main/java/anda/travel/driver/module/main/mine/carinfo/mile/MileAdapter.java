package anda.travel.driver.module.main.mine.carinfo.mile;

import android.content.Context;

import java.util.ArrayList;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.adapter.internal.SuperViewHolder;
import anda.travel.driver.baselibrary.view.refreshview.RefreshAdapter;
import anda.travel.driver.data.entity.MileItemEntity;

class MileAdapter extends RefreshAdapter<MileItemEntity> {

    public MileAdapter(Context context) {
        super(context, new ArrayList<>(), R.layout.hxyc_item_mile);
    }

    @Override
    public void onBind(SuperViewHolder holder, int viewType, int position, MileItemEntity item) {
        holder.setText(R.id.tv_time, item.dataTimeStr);
        holder.setText(R.id.tv_mile, item.mileage);
    }
}
