package anda.travel.driver.module.main.mine.journal;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.adapter.IMulItemViewType;
import anda.travel.driver.baselibrary.adapter.internal.SuperViewHolder;
import anda.travel.driver.baselibrary.utils.DateUtil;
import anda.travel.driver.baselibrary.utils.NumberUtil;
import anda.travel.driver.baselibrary.view.refreshview.RefreshAdapter;
import anda.travel.driver.data.entity.JournalDetailEntity;

class JournalAdapter extends RefreshAdapter<JournalDetailEntity> {

    public static final int JOURNAL_ORDER = 1;
    public static final int JOURNAL_OTHER = 2;

    public JournalAdapter(Context context) {
        super(context, new ArrayList<>(), null);
    }

    @Override
    public void onBind(SuperViewHolder holder, int viewType, int position, JournalDetailEntity item) {
        String curDate = DateUtil.formatDate(new Date(item.getCreateTime()), "yyyy年M月dd日");
        String curTime = DateUtil.formatDate(new Date(item.getCreateTime()), "HH:mm");
        holder.setText(R.id.tv_time, curDate + " " + curTime)
                .setText(R.id.tv_price, NumberUtil.getFormatValue(item.getRewardMoneySum()) + "元");

        switch (viewType) {
            case JOURNAL_ORDER:
                holder.setText(R.id.tv_start, item.getOrderInfo().originAddress)
                        .setText(R.id.tv_end, item.getOrderInfo().destAddress);
                break;
            case JOURNAL_OTHER:
                holder.setText(R.id.tv_des, item.getDescription())
                        .setText(R.id.tv_content, item.getContext());
                break;
        }
    }

    @Override
    protected IMulItemViewType<JournalDetailEntity> offerMultiItemViewType() {
        return new IMulItemViewType<JournalDetailEntity>() {
            @Override
            public int getItemViewType(int position, JournalDetailEntity item) {
                if (item.getType() == 1) {
                    return JOURNAL_ORDER;
                } else {
                    return JOURNAL_OTHER;
                }
            }

            @Override
            public int getLayoutId(int viewType) {
                if (viewType == JOURNAL_ORDER) {
                    return R.layout.hxyc_item_journal;
                }
                return R.layout.hxyc_item_journal_other;
            }
        };
    }
}
