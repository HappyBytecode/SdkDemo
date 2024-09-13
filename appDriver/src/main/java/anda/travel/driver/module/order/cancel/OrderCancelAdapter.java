package anda.travel.driver.module.order.cancel;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.adapter.SuperAdapter;
import anda.travel.driver.baselibrary.adapter.internal.SuperViewHolder;
import anda.travel.driver.baselibrary.utils.TypeUtil;
import anda.travel.driver.data.entity.CancelReasonEntity;

/**
 * 功能描述：
 */
class OrderCancelAdapter extends SuperAdapter<CancelReasonEntity> {
    Context mContext;

    public OrderCancelAdapter(Context context, int layoutId) {
        super(context, new ArrayList<>(), layoutId);
        mContext = context;
    }

    private int mIndex = -1;

    @Override
    public void onBind(SuperViewHolder holder, int viewType, int position, CancelReasonEntity item) {
        TextView tv_content = holder.getView(R.id.tv_content);
        holder.setText(R.id.tv_content, TypeUtil.getValue(item.tagName));
        boolean isSelect = mIndex == position;
        tv_content.setSelected(isSelect);
        if (isSelect) {
            tv_content.setTypeface(Typeface.DEFAULT_BOLD);
            tv_content.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        } else {
            tv_content.setTypeface(Typeface.DEFAULT);
            tv_content.setTextColor(ContextCompat.getColor(mContext, R.color.item_black_primary));
        }
    }

    public void select(int position) {
        mIndex = position;
        notifyDataSetChanged();
    }

    public String getContent() {
        if (mIndex < 0) return "";
        CancelReasonEntity vo = mList.get(mIndex);
        if (vo == null) return "";
        return TypeUtil.getValue(vo.tagName);
    }
}
