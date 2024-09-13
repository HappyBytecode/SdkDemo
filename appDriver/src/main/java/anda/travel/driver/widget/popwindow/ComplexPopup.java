package anda.travel.driver.widget.popwindow;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import anda.travel.driver.R;
import anda.travel.driver.config.OrderStatus;

public class ComplexPopup extends BasePopup<ComplexPopup> {
    private Context mContext;
    private TextView all_order;
    private TextView wait_service;
    private TextView un_paid;
    private TextView paid;
    private TextView popup_order_cancel;
    private ChoiceCallBack mChoiceCallBack;

    public static ComplexPopup create(Context context) {
        return new ComplexPopup(context);
    }

    protected ComplexPopup(Context context) {
        mContext = context;
        setContext(context);
    }

    @Override
    protected void initAttributes() {
        setContentView(R.layout.hxyc_layout_popup_order_list, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusAndOutsideEnable(true)
                .setBackgroundDimEnable(true)
                .setDimValue(0.5f);
    }

    @Override
    protected void initViews(View view, ComplexPopup basePopup) {
        all_order = findViewById(R.id.all_order);
        wait_service = findViewById(R.id.wait_service);
        un_paid = findViewById(R.id.un_paid);
        paid = findViewById(R.id.paid);
        popup_order_cancel = findViewById(R.id.popup_order_cancel);

        all_order.getPaint().setFakeBoldText(true);
        all_order.setTextColor(ContextCompat.getColor(mContext, R.color.popup_item_choose));

        all_order.setOnClickListener(view1 -> {
            if (mChoiceCallBack != null)
                mChoiceCallBack.choiceCallBack(OrderStatus.ROUTING_DEFAULT, mContext.getString(R.string.all_order));
            setAllUnChoose();
            all_order.getPaint().setFakeBoldText(true);
            all_order.setTextColor(ContextCompat.getColor(mContext, R.color.popup_item_choose));
            dismiss();
        });
        wait_service.setOnClickListener(view1 -> {
            if (mChoiceCallBack != null)
                mChoiceCallBack.choiceCallBack(OrderStatus.ROUTING_SERVICING, mContext.getString(R.string.wait_service));
            setAllUnChoose();
            wait_service.getPaint().setFakeBoldText(true);
            wait_service.setTextColor(ContextCompat.getColor(mContext, R.color.popup_item_choose));
            dismiss();
        });
        un_paid.setOnClickListener(view1 -> {
            if (mChoiceCallBack != null)
                mChoiceCallBack.choiceCallBack(OrderStatus.UNPAID, mContext.getString(R.string.un_paid));

            setAllUnChoose();
            un_paid.getPaint().setFakeBoldText(true);
            un_paid.setTextColor(ContextCompat.getColor(mContext, R.color.popup_item_choose));
            dismiss();
        });
        paid.setOnClickListener(view1 -> {
            if (mChoiceCallBack != null)
                mChoiceCallBack.choiceCallBack(OrderStatus.ROUTING_END, mContext.getString(R.string.paid));

            setAllUnChoose();
            paid.getPaint().setFakeBoldText(true);
            paid.setTextColor(ContextCompat.getColor(mContext, R.color.popup_item_choose));
            dismiss();
        });
        popup_order_cancel.setOnClickListener(view1 -> {
            if (mChoiceCallBack != null)
                mChoiceCallBack.choiceCallBack(OrderStatus.ROUTING_CANCEL, mContext.getString(R.string.popup_order_cancel));

            setAllUnChoose();
            popup_order_cancel.getPaint().setFakeBoldText(true);
            popup_order_cancel.setTextColor(ContextCompat.getColor(mContext, R.color.popup_item_choose));
            dismiss();
        });
    }

    private void setAllUnChoose() {
        all_order.getPaint().setFakeBoldText(false);
        wait_service.getPaint().setFakeBoldText(false);
        un_paid.getPaint().setFakeBoldText(false);
        paid.getPaint().setFakeBoldText(false);
        popup_order_cancel.getPaint().setFakeBoldText(false);

        all_order.setTextColor(ContextCompat.getColor(mContext, R.color.popup_item_un_choose));
        wait_service.setTextColor(ContextCompat.getColor(mContext, R.color.popup_item_un_choose));
        un_paid.setTextColor(ContextCompat.getColor(mContext, R.color.popup_item_un_choose));
        paid.setTextColor(ContextCompat.getColor(mContext, R.color.popup_item_un_choose));
        popup_order_cancel.setTextColor(ContextCompat.getColor(mContext, R.color.popup_item_un_choose));
    }

    public void setChoiceCallBack(ChoiceCallBack choiceCallBack) {
        this.mChoiceCallBack = choiceCallBack;
    }

    public interface ChoiceCallBack {
        void choiceCallBack(int type, String result);
    }

}