package anda.travel.driver.module.order.list;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.adapter.internal.SuperViewHolder;
import anda.travel.driver.baselibrary.utils.DateUtil;
import anda.travel.driver.baselibrary.view.refreshview.RefreshAdapter;
import anda.travel.driver.config.HxPayStatus;
import anda.travel.driver.config.OrderStatus;
import anda.travel.driver.module.vo.OrderSummaryVO;
import anda.travel.driver.util.SysConfigUtils;

/**
 * 功能描述：
 */
class OrderListAdapter extends RefreshAdapter<OrderSummaryVO> {

    public OrderListAdapter(Context context, int layoutId) {
        super(context, new ArrayList<>(), layoutId);
    }

    @Override
    public void onBind(SuperViewHolder holder, int viewType, int position, OrderSummaryVO item) {
        String curDate = DateUtil.formatDate(new Date(item.getDepartTime()), "yyyy年M月dd日");
        String curTime = DateUtil.formatDate(new Date(item.getDepartTime()), "HH:mm");
        holder.setText(R.id.tv_time, curDate + " " + curTime)
                .setText(R.id.tv_start, item.getOriginAddress())
                .setText(R.id.tv_end, item.getDestAddress());
        TextView tv_type = holder.getView(R.id.tv_type);
        tv_type.getPaint().setFakeBoldText(true);
        setStatusDisplay(holder, item);
    }

    private void setStatusDisplay(SuperViewHolder holder, OrderSummaryVO item) {
        if (item.mainStatus == null || item.subStatus == null) return;
        String str = "";
        TextView tv_price = holder.getView(R.id.tv_price);
        boolean waitPay = false;
        TextView tv_for_payment = holder.getView(R.id.tv_for_payment);
        switch (item.mainStatus) { //根据主状态判断显示
            case OrderStatus.ORDER_MAIN_STATUS_CANCEL: //订单取消（完结）
                if (item.payStatus != null
                        && item.payStatus == HxPayStatus.FARE_CONFIRMED_NOT_PAY) {
                    str = "未支付";
                    tv_price.setVisibility(View.VISIBLE);
                    tv_price.setText(item.getStrFare());
                    waitPay = true;
                } else {
                    str = item.subStatus != OrderStatus.CLOSE ? "已取消" : "已关闭";
                    if (item.drvTotalFare == null || item.drvTotalFare == 0) {
                        tv_price.setVisibility(View.GONE);
                    } else {
                        tv_price.setVisibility(View.VISIBLE);
                        tv_price.setText(item.getStrFare());
                    }
                }
                break;
            case OrderStatus.ORDER_MAIN_STATUS_PAYED: //支付完成、订单结束
                str = "已支付";
                tv_price.setVisibility(View.VISIBLE);
                tv_price.setText(item.getStrFare());
                break;
            case OrderStatus.ORDER_MAIN_STATUS_DONE: //行程结束、未支付
                str = "未支付";
                tv_price.setVisibility(View.VISIBLE);
                tv_price.setText(item.getStrFare());
                waitPay = true;
                break;
            default:
                switch (item.subStatus) { //根据子状态判断显示
                    case OrderStatus.WAIT_BEGIN_APPOINTMENT:
                        str = "待服务";
                        tv_price.setVisibility(View.GONE);
                        break;
                    case OrderStatus.WAIT_ARRIVE_ORIGIN:
                    case OrderStatus.WATI_PASSENGER_GET_ON:
                    case OrderStatus.DEPART:
                        str = "进行中";
                        break;
                    case OrderStatus.ARRIVE_DEST:
                        str = "未支付";
                        tv_price.setVisibility(View.VISIBLE);
                        tv_price.setText(item.getStrFare());
                        waitPay = true;
                        break;
                }
                break;
        }
        TextView tvStatus = holder.getView(R.id.tv_status);
        tvStatus.setText(str);
        if (item.riskStatus == OrderStatus.RISK_STATUS_YES) {
            holder.getView(R.id.tv_type).setVisibility(View.GONE);
        } else {
            holder.getView(R.id.tv_type).setVisibility(View.GONE);
        }
        if (waitPay) {
            if (item.canHurryPay == OrderStatus.CAN_HURRY_PAY &&
                    null != SysConfigUtils.get().getSysConfig() &&
                    SysConfigUtils.get().getSysConfig().getHintPaymentSwitch().equals("1")) {
                tv_for_payment.setVisibility(View.GONE);
            } else {
                tv_for_payment.setVisibility(View.GONE);
            }
        } else {
            tv_for_payment.setVisibility(View.GONE);
        }
    }
}
