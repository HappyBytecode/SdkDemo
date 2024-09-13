package anda.travel.driver.module.main.mine.walletnew.withdrawal.withdrawalrecord;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.adapter.internal.SuperViewHolder;
import anda.travel.driver.baselibrary.utils.NumberUtil;
import anda.travel.driver.baselibrary.view.refreshview.RefreshAdapter;
import anda.travel.driver.module.vo.WithdrawaleRecordVO;

class WithdrawalRecordAdapter extends RefreshAdapter<WithdrawaleRecordVO> {
    private final SimpleDateFormat sf;

    @SuppressLint("SimpleDateFormat")
    public WithdrawalRecordAdapter(Context context) {
        super(context, new ArrayList<>(), R.layout.hxyc_item_withdrawal_record);
        sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    @Override
    public void onBind(SuperViewHolder holder, int viewType, int position, WithdrawaleRecordVO item) {
        ((TextView) holder.getView(R.id.tv_title)).setText(MessageFormat.format("提现{0}", getContext()
                .getResources().getString(R.string.withdraw_fee, NumberUtil.getFormatValue(Math.abs(item.money)))));
        holder.setText(R.id.tv_date, sf.format(new Date(item.date)));
        switch (item.statue) {
            case 1:
            case 10:
                holder.setText(R.id.tv_statue, "待审核");
                break;
            case 2:
                holder.setText(R.id.tv_statue, "提现成功");
                break;
            case 3:
                holder.setText(R.id.tv_statue, "提现失败");
                break;
            case 4:
            case 20:
                holder.setText(R.id.tv_statue, "打款中");
                break;
            case 30:
                holder.setText(R.id.tv_statue, "打款成功");
                break;
            case 40:
                holder.setText(R.id.tv_statue, "打款失败");
                break;
            case 50:
                holder.setText(R.id.tv_statue, "审核拒绝");
                break;
            case 60:
                holder.setText(R.id.tv_statue, "审核异常");
                break;
            default:
                break;
        }
    }
}
