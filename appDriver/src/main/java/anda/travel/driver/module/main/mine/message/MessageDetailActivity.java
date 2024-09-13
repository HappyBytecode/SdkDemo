package anda.travel.driver.module.main.mine.message;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Date;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.utils.DateUtil;
import anda.travel.driver.baselibrary.utils.GlideRoundTransform;
import anda.travel.driver.baselibrary.view.HeadView;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.config.HxMessageType;
import anda.travel.driver.data.entity.HxMessageEntity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageDetailActivity extends BaseActivity {

    @BindView(R2.id.head_view)
    HeadView mHeadView;
    @BindView(R2.id.tv_time)
    TextView mTvTime;
    @BindView(R2.id.iv_pic)
    ImageView mIvPic;
    @BindView(R2.id.tv_content)
    TextView mTvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_message_detail);
        ButterKnife.bind(this);
        showDetail((HxMessageEntity) getIntent().getSerializableExtra("message"));
    }

    private void showDetail(HxMessageEntity msgEntity) {
        String title = "";
        if (msgEntity.getType() == HxMessageType.MSG_SYSTEM) {
            title = "系统消息";
        }
        mHeadView.setTitle(title);
        String curTime = msgEntity.getPushTime() == null || msgEntity.getPushTime() <= 0
                ? ""
                : DateUtil.formatDate(new Date(msgEntity.getPushTime()), "MM月dd日 HH:mm");
        mTvTime.setText(curTime);
        Glide.with(this).load(msgEntity.getPic())
                .placeholder(R.drawable.msg_default)
                .transform(new GlideRoundTransform(5))
                .into(mIvPic);
        mTvContent.setText(msgEntity.getContent());
    }
}
