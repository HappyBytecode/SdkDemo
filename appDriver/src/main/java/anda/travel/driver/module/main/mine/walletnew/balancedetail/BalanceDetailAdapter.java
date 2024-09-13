package anda.travel.driver.module.main.mine.walletnew.balancedetail;

import android.content.Context;
import android.view.View;

import java.text.MessageFormat;
import java.util.ArrayList;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.adapter.internal.SuperViewHolder;
import anda.travel.driver.baselibrary.utils.NumberUtil;
import anda.travel.driver.baselibrary.view.refreshview.RefreshAdapter;
import anda.travel.driver.data.entity.BalanceDetailListEntity;
import anda.travel.driver.util.TimeUtils;

class BalanceDetailAdapter extends RefreshAdapter<BalanceDetailListEntity> {

    private final Context mContext;

    public BalanceDetailAdapter(Context context) {
        super(context, new ArrayList<>(), R.layout.hxyc_item_balance_detail);
        mContext = context;
    }

    @Override
    public void onBind(SuperViewHolder holder, int viewType, int position, BalanceDetailListEntity item) {
        holder.setVisibility(R.id.cons_content, item.isTotal ? View.GONE : View.VISIBLE);
        holder.setVisibility(R.id.rll_top, item.isTotal ? View.VISIBLE : View.GONE);
        if (item.isTotal) {
            holder.setText(R.id.tv_date, item.detailDate)
                    .setText(R.id.tv_total_amount, MessageFormat.format("合计 {0}元", NumberUtil.getFormatValue(item.total)));
        } else {
            holder.setText(R.id.tv_balance_detail_type, item.name)
                    .setText(R.id.tv_balance_detail_amount, NumberUtil.getFormatValue(item.amount))
                    .setText(R.id.tv_balance_detail_time, TimeUtils.paseDateAndTime(item.operateTime))
                    .setText(R.id.tv_balance_detail_action, item.remark);
            holder.setTextColor(R.id.tv_balance_detail_action,
                    "平台垫付".equals(item.remark) ? mContext.getResources().getColor(R.color.color_accent) :
                            mContext.getResources().getColor(R.color.text_black));
        }
    }
}
