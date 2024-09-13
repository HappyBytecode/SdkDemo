package anda.travel.driver.module.car;

import android.content.Context;
import android.widget.TextView;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.utils.DisplayUtil;
import anda.travel.driver.baselibrary.view.dialog.ExSweetAlertDialog;
import anda.travel.driver.baselibrary.view.wheel.adapter.ArrayWheelAdapter;
import anda.travel.driver.baselibrary.view.wheel.hh.WheelView;

/**
 * @Author moyuwan
 * @Date 18/3/6
 */
class SelectBelongDialog extends ExSweetAlertDialog {

    private final SelectListener mListener;
    private final TextView tv_sure;
    private final WheelView wheel1;
    private final WheelView wheel2;

    private final String[] array1;
    private final String[] array2;

    public SelectBelongDialog(Context context, SelectListener listener) {
        super(context, R.layout.hxyc_dialog_select_belong);
        setWidth(DisplayUtil.getScreenW(context));
        setHeight(DisplayUtil.getScreenH(context) - DisplayUtil.getStatusHeight(context));
        setAnimIn(R.anim.dialog_selecter_in);
        setAnimOut(R.anim.dialog_selecter_out);

        mListener = listener;
        wheel1 = (WheelView) findViewById(R.id.wheel1);
        wheel2 = (WheelView) findViewById(R.id.wheel2);
        TextView tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_sure = (TextView) findViewById(R.id.tv_sure);
        findViewById(R.id.view_bg).setOnClickListener(view -> dismiss());
        tv_cancel.setOnClickListener(view -> dismiss());
        tv_sure.setOnClickListener(view -> {
            mListener.onSelect(getValue());
            dismiss();
        });

        wheel1.setVisibleItems(5);
        array1 = context.getResources().getStringArray(R.array.car_belong_array1);
        ArrayWheelAdapter<String> adapter1 = new ArrayWheelAdapter<>(getContext(), array1);
        wheel1.setViewAdapter(adapter1);
        wheel2.setVisibleItems(5);
        array2 = context.getResources().getStringArray(R.array.car_belong_array2);
        ArrayWheelAdapter<String> adapter2 = new ArrayWheelAdapter<>(getContext(), array2);
        wheel2.setViewAdapter(adapter2);
    }

    interface SelectListener {
        void onSelect(String value);
    }

    private String getValue() {
        StringBuilder builder = new StringBuilder();
        int index1 = wheel1.getCurrentItem();
        int index2 = wheel2.getCurrentItem();
        builder.append((array1 != null && index1 < array1.length) ? array1[index1] : "");
        builder.append((array2 != null && index2 < array2.length) ? array2[index2] : "");
        return builder.toString();
    }
}
