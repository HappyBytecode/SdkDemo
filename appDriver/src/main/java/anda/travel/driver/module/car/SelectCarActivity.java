package anda.travel.driver.module.car;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.utils.TypeUtil;
import anda.travel.driver.baselibrary.view.HeadView;
import anda.travel.driver.baselibrary.view.TextViewPlus;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.data.entity.DriverEntity;
import anda.travel.driver.event.DutyEvent;
import anda.travel.driver.module.car.dagger.DaggerSelectCarComponent;
import anda.travel.driver.module.car.dagger.SelectCarModule;
import anda.travel.driver.util.AllCapTransformationMethod;
import anda.travel.driver.util.FilterUtils;
import anda.travel.driver.util.Navigate;
import anda.travel.driver.util.RefreshUtil;
import anda.travel.driver.widget.CircleImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author moyuwan
 * @Date 18/3/4
 * <p>
 * 代班司机，选择车辆页
 */
public class SelectCarActivity extends BaseActivity implements SelectCarContract.View, TextWatcher {

    private final static String FROM_TYPE = "FROM_TYPE";
    private final static int TYPE_SELECT = 0; //更改车辆
    public final static int TYPE_LOGIN = 1; //登录后触发
    public final static int TYPE_DUTY = 2; //出车时触发

    public static void actionStart(Context context) {
        actionStart(context, TYPE_SELECT);
    }

    public static void actionStart(Context context, int fromType) {
        Intent intent = new Intent(context, SelectCarActivity.class);
        intent.putExtra(FROM_TYPE, fromType);
        context.startActivity(intent);
    }

    @BindView(R2.id.head_view)
    HeadView mHeadView;
    @BindView(R2.id.iv_avatar)
    CircleImageView mIvAvatar;
    @BindView(R2.id.tv_name)
    TextView mTvName;
    @BindView(R2.id.tv_belong)
    TextViewPlus mTvBelong;
    @BindView(R2.id.ed_input)
    EditText mEdInput;
    @BindView(R2.id.btn_submit)
    AppCompatButton mBtnSubmit;

    private int mFromType;

    @Inject
    SelectCarPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_select_car);
        ButterKnife.bind(this);
        DaggerSelectCarComponent.builder()
                .appComponent(getAppComponent())
                .selectCarModule(new SelectCarModule(this))
                .build().inject(this);

        mFromType = getIntent().getIntExtra(FROM_TYPE, TYPE_SELECT);
        mHeadView.getLeftView().setOnClickListener(view -> {
            if (mFromType == TYPE_LOGIN) { //按返回键，跳转到首页
                Navigate.openMain(this, TYPE_LOGIN);
            }
            finish();
        });
        mEdInput.setTransformationMethod(new AllCapTransformationMethod());
        mEdInput.addTextChangedListener(this);
        setDisplay();
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
    public void setDisplay() {
        DriverEntity entity = mPresenter.getDriverEntity();
        if (entity == null) return;
        Glide.with(this).load(TypeUtil.getValue(entity.avatar)).into(mIvAvatar);
        mTvName.setText(TypeUtil.getValue(entity.actualName));
        if (entity.vehicleNo != null && entity.vehicleNo.length() >= 2) {
            mTvBelong.setText(entity.vehicleNo.substring(0, 2));
            if (entity.vehicleNo.length() > 2) {
                mEdInput.setText(entity.vehicleNo.substring(2));
                Selection.setSelection(mEdInput.getEditableText(), mEdInput.getEditableText().length());
            }
        }
    }

    @Override
    public void selectCarSuccess() {
        RefreshUtil.setRefresh(true);
        if (mFromType == TYPE_LOGIN) {
            Navigate.openMain(this, TYPE_LOGIN);
        } else if (mFromType == TYPE_DUTY) {
            EventBus.getDefault().post(new DutyEvent(DutyEvent.ON_DUTY));
        } else if (mFromType == TYPE_SELECT) {
            EventBus.getDefault().post(new DutyEvent(DutyEvent.REFRESH_DUTY));
        }
        finish();
    }

    @OnClick({R2.id.tv_belong, R2.id.btn_submit})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.tv_belong) {
            new SelectBelongDialog(this, value -> {
                mTvBelong.setText(value);
            }).show();
        } else if (id == R.id.btn_submit) {
            mPresenter.selectCar(mTvBelong.getText().toString().trim() +
                    mEdInput.getEditableText().toString().trim().toUpperCase());
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        // 限定只能输入数字或字母
        String editable = mEdInput.getText().toString();
        String str = FilterUtils.carNoFilter(editable);
        if (!editable.equals(str)) {
            mEdInput.setText(str);
            // 设置新的光标所在位置
            mEdInput.setSelection(str.length());
        }
        mBtnSubmit.setEnabled(str.length() == 5 || str.length() == 6);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

}
