package anda.travel.driver.module.order.complain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.view.HeadView;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.data.entity.ComplainEntity;
import anda.travel.driver.module.order.complain.dagger.DaggerOrderComplainComponent;
import anda.travel.driver.module.order.complain.dagger.OrderComplainModule;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 功能描述：
 */
@SuppressLint("NonConstantResourceId")
public class OrderComplainActivity extends BaseActivity implements OrderComplainContract.View {

    public static void actionStart(Context context, String orderUuid) {
        Intent intent = new Intent(context, OrderComplainActivity.class);
        intent.putExtra(IConstants.ORDER_UUID, orderUuid);
        context.startActivity(intent);
    }

    @Inject
    OrderComplainPresenter mPresenter;

    @BindView(R2.id.head_view)
    HeadView mHeadView;
    @BindView(R2.id.et_complain_remark)
    EditText mEtComplainRemark;
    @BindView(R2.id.tv_account)
    TextView mTvAccount;
    @BindView(R2.id.tfl_remark)
    TagFlowLayout mTagFlowLayout;
    @BindView(R2.id.layout_remark)
    ConstraintLayout mLayoutRemark;
    @BindView(R2.id.tv_complaint_title)
    TextView mTvComplaintTitle;
    @BindView(R2.id.tv_status_remark)
    TextView mTvStatusRemark;
    @BindView(R2.id.tv_submit)
    TextView mTvSubmit;
    @BindView(R2.id.tv_complaint_status)
    TextView mTvComplaintStatus;
    @BindView(R2.id.tv_complaint_desc)
    TextView mTvComplaintDesc;

    private List<ComplainEntity> mTags;
    private boolean isSubmit;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_order_complain);
        ButterKnife.bind(this);
        DaggerOrderComplainComponent.builder().appComponent(getAppComponent())
                .orderComplainModule(new OrderComplainModule(this)).build().inject(this);
        mPresenter.setOrderUuid(getIntent().getStringExtra(IConstants.ORDER_UUID));
        mEtComplainRemark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String content = charSequence.toString();
                if (TextUtils.isEmpty(content)) {
                    mTvAccount.setText("0/120字");
                } else {
                    mTvAccount.setText(content.length() + "/120字");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mTags = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.unsubscribe();
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void showComplainMsg(List<ComplainEntity> entities) {
        if (mTags != null && !mTags.isEmpty()) {
            mTags.clear();
        }
        mTags.addAll(entities);
        setTagMessage(true);
    }

    private void setTagMessage(boolean canChecked) {
        mTagFlowLayout.setAdapter(new TagAdapter<ComplainEntity>(mTags) {
            @Override
            public View getView(FlowLayout parent, int position, ComplainEntity data) {
                TextView tv = (TextView) LayoutInflater.from(OrderComplainActivity.this).inflate(R.layout.hxyc_layout_complain_tag,
                        mTagFlowLayout, false);
                tv.setText(data.tagName);
                if (!canChecked) {
                    tv.setBackgroundResource(R.drawable.dra_tag_complain_select);
                    tv.setTextColor(ContextCompat.getColor(OrderComplainActivity.this, R.color.color_accent));
                    tv.setTypeface(Typeface.DEFAULT_BOLD);
                }
                return tv;
            }

            @Override
            public void onSelected(int position, View view) {
                if (isSubmit) return;
                if (canChecked) {
                    view.setBackgroundResource(R.drawable.dra_tag_complain_select);
                    ((TextView) view).setTextColor(ContextCompat.getColor(OrderComplainActivity.this, R.color.color_accent));
                    ((TextView) view).setTypeface(Typeface.DEFAULT_BOLD);
                }
                super.onSelected(position, view);
            }

            @Override
            public void unSelected(int position, View view) {
                if (isSubmit) return;
                super.unSelected(position, view);
                if (canChecked) {
                    view.setBackgroundResource(R.drawable.dra_tag_complain_un_select);
                    ((TextView) view).setTextColor(ContextCompat.getColor(OrderComplainActivity.this, R.color.item_black_primary));
                    ((TextView) view).setTypeface(Typeface.DEFAULT);
                }
            }
        });
    }

    @Override
    public void complainSuccess(String content, String remark) {
        toast("提交成功");
        isSubmit = true;
        setComplainDisplay(content, remark, getResources().getString(R.string.order_complain_dealing), true, false);
    }

    @Override
    public void complained(String content, String remark, String complainResult, boolean isCommit, boolean isDealing) {
        setComplainDisplay(content, remark, complainResult, isCommit, isDealing);
    }

    // 设置投诉的显示
    private void setComplainDisplay(String content, String remark, String complainResult, boolean isCommit, boolean isDealing) {
        /////根据是否提交过判断控件的显示
        if (isCommit) {
            mTvComplaintTitle.setText("您的投诉");
            if (!TextUtils.isEmpty(remark)) {
                visible(mTvStatusRemark);
                mTvStatusRemark.setText(remark);
            }
            gone(mLayoutRemark);
            gone(mTvSubmit);
            visible(mTvComplaintStatus);
            visible(mTvComplaintDesc);
            if (isDealing) {
                mTvComplaintStatus.setText("投诉反馈:");
            } else {
                mTvComplaintStatus.setText(R.string.order_complain_ing);
            }
            if (!TextUtils.isEmpty(complainResult)) {
                mTvComplaintDesc.setText(complainResult);
            } else {
                mTvComplaintDesc.setText(R.string.push_success);
            }
            getTagMessage(content, false);
        } else {
            mTvComplaintTitle.setText(getString(R.string.order_complain_reason));
            gone(mTvStatusRemark);
            visible(mLayoutRemark);
            visible(mTvSubmit);
            gone(mTvComplaintStatus);
            gone(mTvComplaintDesc);
            mPresenter.reqComplainMsg(); //获取投诉标签
        }
    }

    @OnClick(R2.id.tv_submit)
    public void click(View view) {
        if (view.getId() == R.id.tv_submit) {
            String remark = mEtComplainRemark.getText().toString();
            String content = getTagContent();
            mPresenter.submitComplain(content, remark);
        }
    }

    private String getTagContent() {
        StringBuffer sb = new StringBuffer();
        if (mTagFlowLayout.getSelectedList() != null && mTagFlowLayout.getSelectedList().size() > 0) {
            if (mTags != null) {
                for (Integer integer : mTagFlowLayout.getSelectedList()) {
                    sb.append(mTags.get(integer).tagName);
                    sb.append(",");
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } else {
            return "";
        }
    }

    public void getTagMessage(String content, boolean canChecked) {
        if (!TextUtils.isEmpty(content)) {
            String[] temp = content.split(",");
            if (mTags != null && !mTags.isEmpty()) {
                mTags.clear();
            }
            for (String str : temp) {
                mTags.add(new ComplainEntity(str));
            }
            setTagMessage(canChecked);

        } else {
            gone(mTagFlowLayout);
        }
    }
}
