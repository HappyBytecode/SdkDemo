package anda.travel.driver.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.utils.SpannableWrap;

public class MyWalletCommonDialog extends Dialog {
    private TextView tips_confirm;
    private TextView tips_sub_title;
    private TextView tips_title;
    private TextView tips_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.height = WindowManager.LayoutParams.WRAP_CONTENT;
        p.width = (int) (d.getWidth() * 0.85);
        getWindow().setAttributes(p);
    }

    public MyWalletCommonDialog(Context context, OnClickListener closeListener) {
        super(context, R.style.alert_dialog);
        setContentView(R.layout.hxyc_dialog_my_wallet_common);
        tips_confirm = findViewById(R.id.tips_confirm);
        tips_sub_title = findViewById(R.id.tips_sub_title);
        tips_title = findViewById(R.id.tips_title);
        tips_content = findViewById(R.id.tips_content);
        tips_confirm.setOnClickListener(v -> {
            dismiss();
            closeListener.onclick();
        });
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    public MyWalletCommonDialog setSubTitle(String subTitle) {
        if (!TextUtils.isEmpty(subTitle)) {
            tips_sub_title.setVisibility(View.VISIBLE);
            tips_sub_title.setText(subTitle);
        } else {
            tips_sub_title.setVisibility(View.GONE);
        }
        return this;
    }

    public MyWalletCommonDialog setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            tips_title.setVisibility(View.VISIBLE);
            tips_title.setText(title);
        } else {
            tips_title.setVisibility(View.GONE);
        }
        return this;
    }

    public MyWalletCommonDialog setContent(String content) {
        if (!TextUtils.isEmpty(content)) {
            tips_content.setVisibility(View.VISIBLE);
            tips_content.setText(content);
        } else {
            tips_content.setVisibility(View.GONE);
        }
        return this;
    }

    public MyWalletCommonDialog setHtmlContent(String content) {
        if (!TextUtils.isEmpty(content)) {
            tips_content.setVisibility(View.VISIBLE);
            tips_content.setText(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N ?
                    Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY)
                    : Html.fromHtml(content));
        } else {
            tips_content.setVisibility(View.GONE);
        }
        return this;
    }

    public MyWalletCommonDialog setContent(String content1, String content2) {
        if (!TextUtils.isEmpty(content1)) {
            tips_content.setVisibility(View.VISIBLE);
            SpannableWrap.setText(content1)
                    .append(content2)
                    .size(24, true)
                    .textColor(getContext().getResources().getColor(R.color.journal_black_dark))
                    .into(tips_content);
            tips_content.setGravity(Gravity.CENTER);
        } else {
            tips_content.setVisibility(View.GONE);
        }
        return this;
    }

    public interface OnClickListener {
        void onclick();
    }

}



