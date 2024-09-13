package anda.travel.driver.module.order.pay;

import android.content.Context;
import android.widget.TextView;

import java.util.ArrayList;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.adapter.SuperAdapter;
import anda.travel.driver.baselibrary.adapter.internal.SuperViewHolder;
import anda.travel.driver.data.entity.PayTypeEntity;

class OrderPayAdapter extends SuperAdapter<PayTypeEntity> {

    public OrderPayAdapter(Context context) {
        super(context, new ArrayList<>(), anda.travel.driver.R.layout.hxyc_item_pay_type);
    }

    @Override
    public void onBind(SuperViewHolder holder, int viewType, int position, PayTypeEntity item) {
        String payType = item.getPayType();
        if (payType.contains("余额")) {
            ((TextView) holder.getView(R.id.tv_item)).setText(R.string.order_pay_balance);
            holder.setImageResource(R.id.img_item, R.drawable.zhifu_icon_yuer);
        } else if (payType.contains("微信支付")) {
            ((TextView) holder.getView(R.id.tv_item)).setText(R.string.order_pay_wechat);
            holder.setImageResource(R.id.img_item, R.drawable.zhifu_icon_weixin);
        } else {
            ((TextView) holder.getView(R.id.tv_item)).setText(R.string.order_pay_alipay);
            holder.setImageResource(R.id.img_item, R.drawable.zhifu_icon_zhifubao);
        }
    }
}
