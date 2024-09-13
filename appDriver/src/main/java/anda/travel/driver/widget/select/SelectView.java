package anda.travel.driver.widget.select;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import anda.travel.driver.R;

/**
 * 功能描述：
 */
public class SelectView extends LinearLayout implements View.OnClickListener {

    private static final int[] mAttr = {android.R.attr.hint};

    private TextView tv;
    private TextView tvDel;
    private ActionListener mListener;

    public SelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, mAttr);
        String hint = ta.getString(0);
        ta.recycle();
        initView(context, hint);
    }

    private void initView(Context context, String hint) {
        View mView = LayoutInflater.from(context).inflate(R.layout.hxyc_layout_selectview, this);
        tv = mView.findViewById(R.id.tv);
        tvDel = mView.findViewById(R.id.tvDel);
        tv.setOnClickListener(this);
        tvDel.setOnClickListener(this);
        tv.setHint(hint);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv) {
            if (mListener != null) mListener.onClick(this);
        } else if (id == R.id.tvDel) {
            setText(""); //清空内容
            if (mListener != null) mListener.onDelete(this);
        }
    }

    public void setText(String content) {
        tv.setText(content); //设置内容
        if (TextUtils.isEmpty(content)) {
            tvDel.setVisibility(View.GONE);
            tv.setSelected(false);
        } else {
            tvDel.setVisibility(View.VISIBLE);
            tv.setSelected(true);
        }
    }

    public void setOnActionListener(ActionListener listener) {
        this.mListener = listener;
    }

    public interface ActionListener {
        void onClick(SelectView view);

        void onDelete(SelectView view);
    }

}
