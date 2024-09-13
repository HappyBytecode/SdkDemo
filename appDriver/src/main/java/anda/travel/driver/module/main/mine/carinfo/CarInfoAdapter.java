package anda.travel.driver.module.main.mine.carinfo;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.adapter.SuperAdapter;
import anda.travel.driver.baselibrary.adapter.internal.SuperViewHolder;
import anda.travel.driver.data.entity.CarItemEntity;

class CarInfoAdapter extends SuperAdapter<CarItemEntity> {

    public CarInfoAdapter(Context context) {
        super(context, new ArrayList<>(), R.layout.hxyc_item_car_info);
    }

    @Override
    public void onBind(SuperViewHolder holder, int viewType, int position, CarItemEntity item) {
        holder.setText(R.id.tv_key, item.key);
        holder.setText(R.id.tv_value, item.value);
        if (item.key.equals("累计里程")) {
            holder.getView(R.id.iv_arrow).setVisibility(View.VISIBLE);
        }
    }
}
