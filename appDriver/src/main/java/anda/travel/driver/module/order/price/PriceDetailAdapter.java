package anda.travel.driver.module.order.price;

import android.content.Context;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.adapter.SuperAdapter;
import anda.travel.driver.baselibrary.adapter.internal.SuperViewHolder;
import anda.travel.driver.baselibrary.utils.NumberUtil;
import anda.travel.driver.baselibrary.utils.SpannableWrap;
import anda.travel.driver.baselibrary.utils.TypeUtil;
import anda.travel.driver.data.entity.OrderCostItemEntity;

/**
 * 功能描述：
 */
public class PriceDetailAdapter extends SuperAdapter<OrderCostItemEntity> {
    private final Context mContext;

    public PriceDetailAdapter(Context context, int layoutId) {
        super(context, new ArrayList<>(), layoutId);
        mContext = context;
    }

    @Override
    public void onBind(SuperViewHolder holder, int viewType, int position, OrderCostItemEntity item) {
        holder.setText(R.id.tv_tag, TypeUtil.getValue(item.item));
        TextView tv_value = holder.getView(R.id.tv_value);
        SpannableWrap.setText(NumberUtil.getFormatValue(item.cost, true))
                .textColor(ContextCompat.getColor(mContext, R.color.popup_item_un_choose))
                .append("元")
                .textColor(ContextCompat.getColor(mContext, R.color.text_black))
                .into(tv_value);
    }
}
