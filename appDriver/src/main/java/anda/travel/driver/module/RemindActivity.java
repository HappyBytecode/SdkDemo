package anda.travel.driver.module;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author zy
 * @Date 19/12/6
 */
@SuppressLint("NonConstantResourceId")
public class RemindActivity extends Activity {

    @BindView(R2.id.remind_title)
    TextView mTvTitle;
    @BindView(R2.id.remind_content)
    TextView mTvContent;

    public static void actionStart(Context context, String title, String content) {
        Intent intent = new Intent(context, RemindActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_remind);
        this.setFinishOnTouchOutside(false);//设置窗口周围触摸不消失
        getWindow().setDimAmount(0.6f);//设置窗口周围透明
        ButterKnife.bind(this);
        String mTitle = getIntent().getStringExtra("title");
        String mContent = getIntent().getStringExtra("content");
        mTvTitle.setText(mTitle);
        mTvContent.setText(mContent);
    }

    @OnClick(R2.id.remind_close)
    public void onClick(View view) {
        if (view.getId() == R.id.remind_close) {
            finish();
        }
    }
}
