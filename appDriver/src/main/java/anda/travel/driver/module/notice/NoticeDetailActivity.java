package anda.travel.driver.module.notice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.view.HeadView;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.data.entity.NoticeEntity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NoticeDetailActivity extends BaseActivity {

    @BindView(R2.id.head_view)
    HeadView mHeadView;
    @BindView(R2.id.tv_content)
    TextView mTvContent;

    public static void actionStart(Context context, NoticeEntity entity) {
        Intent intent = new Intent(context, NoticeDetailActivity.class);
        intent.putExtra("data", entity);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_notice_detail);
        ButterKnife.bind(this);
        NoticeEntity mNotice = (NoticeEntity) getIntent().getSerializableExtra("data");
        setDisplay(mNotice);
    }

    private void setDisplay(NoticeEntity entity) {
        if (entity == null) {
            return;
        }
        mHeadView.setTitle(entity.getTitle());
        String str = entity.getContent();
        if (TextUtils.isEmpty(str)) str = entity.getTitle();
        mTvContent.setText(str);
    }
}
