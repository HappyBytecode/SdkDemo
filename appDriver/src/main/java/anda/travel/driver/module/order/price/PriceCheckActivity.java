package anda.travel.driver.module.order.price;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.text.MessageFormat;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.utils.NumberUtil;
import anda.travel.driver.baselibrary.utils.SpannableWrap;
import anda.travel.driver.baselibrary.utils.ToastUtil;
import anda.travel.driver.baselibrary.utils.TypeUtil;
import anda.travel.driver.baselibrary.view.HeadView;
import anda.travel.driver.common.AppConfig;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.config.OrderStatus;
import anda.travel.driver.configurl.MyConfig;
import anda.travel.driver.configurl.ParseUtils;
import anda.travel.driver.data.entity.OrderCostEntity;
import anda.travel.driver.data.entity.SysConfigEntity;
import anda.travel.driver.module.order.price.dagger.DaggerPriceCheckComponent;
import anda.travel.driver.module.order.price.dagger.PriceCheckModule;
import anda.travel.driver.module.vo.OrderVO;
import anda.travel.driver.module.web.WebActivity;
import anda.travel.driver.util.MoneyValueFilter;
import anda.travel.driver.util.Navigate;
import anda.travel.driver.util.SpeechUtil;
import anda.travel.driver.util.SysConfigUtils;
import anda.travel.driver.view.CommonBottomSheetDialog;
import anda.travel.driver.widget.CommonAlertDialog;
import anda.travel.driver.widget.slide.SlideView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class PriceCheckActivity extends BaseActivity implements PriceCheckContract.View {

    public static void actionStart(Context context, String orderUuid, OrderVO orderVO) {
        Intent intent = new Intent(context, PriceCheckActivity.class);
        intent.putExtra(IConstants.ORDER_UUID, orderUuid);
        if (orderVO != null) intent.putExtra(IConstants.PARAMS, orderVO);
        context.startActivity(intent);
    }
    public static void actionStart1(Context context, String orderUuid, OrderVO orderVO) {
        Intent intent = new Intent(context, PriceCheckActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(IConstants.ORDER_UUID, orderUuid);
        if (orderVO != null) intent.putExtra(IConstants.PARAMS, orderVO);
        context.startActivity(intent);
    }

    @BindView(R2.id.head_view)
    HeadView mHeadView;
    @BindView(R2.id.tv_price)
    TextView mTvPrice;
    @BindView(R2.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R2.id.slide_view)
    SlideView mSlideView;
    @BindView(R2.id.et_high_way)
    EditText mEtHighWay;
    @BindView(R2.id.et_bridge)
    EditText mEtBridge;
    @BindView(R2.id.et_parking)
    EditText mEtParking;

    @Inject
    PriceCheckPresenter mPresenter;
    private PriceDetailAdapter mAdapter;

    private String mOrderUuid; //订单id
    private String mBusinessUuid; // 业务线id
    private Double mPrice; // 临时记录的总价，继续计费时传递

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_price_check);
        ButterKnife.bind(this);
        DaggerPriceCheckComponent.builder().appComponent(getAppComponent())
                .priceCheckModule(new PriceCheckModule(this)).build().inject(this);

        mHeadView.getRightTextView().setOnClickListener(v -> {
            MyConfig config = ParseUtils.getInstance().getMyConfig();
            if (config == null || TextUtils.isEmpty(config.getPriceRules())) {
                toast("未获取到计价规则");
                return;
            }
            WebActivity.actionStart(this, config.getPriceRules() +
                            "?appid=" + AppConfig.ANDA_APPKEY +
                            "&busiUuid=" + mBusinessUuid +
                            "&isDriver=2" +
                            (TextUtils.isEmpty(mOrderUuid) ? "" : "&orderUuid=" + mOrderUuid),
                    "计价规则");
        });

        mPresenter.setOrderUuid(getIntent().getStringExtra(IConstants.ORDER_UUID)); //设置订单比编号
        mPresenter.onCreate();

        mAdapter = new PriceDetailAdapter(this, R.layout.hxyc_item_order_cost);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setNestedScrollingEnabled(false); //解决"滑动时粘连/滑动卡顿"的问题
        mSlideView.setOnSlideListener(() -> {
            mPresenter.confirmFare(highwayFare, bridgeFare, parkingFare); //确认费用
        });
        mOrderUuid = getIntent().getStringExtra(IConstants.ORDER_UUID);
        Serializable obj = getIntent().getSerializableExtra(IConstants.PARAMS);
        if (obj != null) {
            OrderVO mOrderVO = (OrderVO) obj;
            mBusinessUuid = mOrderVO.getBusiUuid(); //获取业务线uuid
        }
        SysConfigEntity sysConfigEntity = SysConfigUtils.get().getSysConfig();
        if (null != sysConfigEntity) {
            highwayFareLimit =
                    Double.parseDouble(sysConfigEntity.getHighwayFareTopLimit());
            bridgeFareLimit =
                    Double.parseDouble(sysConfigEntity.getBridgeFareTopLimit());
            parkingFareLimit =
                    Double.parseDouble(sysConfigEntity.getParkingFareTopLimit());
        }

        mEtHighWay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence.toString())) {
                    highwayFare = Double.parseDouble(charSequence.toString());
                    if (highwayFare > highwayFareLimit) {
                        highwayFare = highwayFareLimit;
                        mEtHighWay.setText(MessageFormat.format("{0}", highwayFare));
                        mEtHighWay.setSelection(mEtHighWay.getText().toString().length());

                        ToastUtil.getInstance().showTwoLinesToast(PriceCheckActivity.this,
                                MessageFormat.format("最高不能超过{0}元", highwayFare), "如有问题请联系客服", 5000);
                    }
                } else {
                    highwayFare = 0;
                }
                setTotalFareDisplay();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mEtBridge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence.toString())) {
                    bridgeFare = Double.parseDouble(charSequence.toString());
                    if (bridgeFare > bridgeFareLimit) {
                        bridgeFare = bridgeFareLimit;
                        mEtBridge.setText(MessageFormat.format("{0}", bridgeFare));
                        mEtBridge.setSelection(mEtBridge.getText().toString().length());
                        ToastUtil.getInstance().showTwoLinesToast(PriceCheckActivity.this,
                                MessageFormat.format("最高不能超过{0}元", bridgeFare), "如有问题请联系客服", 5000);
                    }
                } else {
                    bridgeFare = 0;
                }
                setTotalFareDisplay();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mEtParking.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence.toString())) {
                    parkingFare = Double.parseDouble(charSequence.toString());
                    if (parkingFare > parkingFareLimit) {
                        parkingFare = parkingFareLimit;
                        mEtParking.setText(MessageFormat.format("{0}", parkingFare));
                        mEtParking.setSelection(mEtParking.getText().toString().length());
                        ToastUtil.getInstance().showTwoLinesToast(PriceCheckActivity.this,
                                MessageFormat.format("最高不能超过{0}元", parkingFare), "如有问题请联系客服", 5000);
                    }
                } else {
                    parkingFare = 0;
                }
                setTotalFareDisplay();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mEtHighWay.setFilters(new InputFilter[]{new MoneyValueFilter()});
        mEtBridge.setFilters(new InputFilter[]{new MoneyValueFilter()});
        mEtParking.setFilters(new InputFilter[]{new MoneyValueFilter()});

        mPresenter.subscribe();
        resetDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.unsubscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestory();
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @OnClick({R2.id.tv_error})
    public void onClick(View view) {
        if (view.getId() == R.id.tv_error) {
            new CommonAlertDialog(this).builder()
                    .setTitle("若您或乘客对费用有异议，可协商后联系客服进行修改。")
                    .setPositiveButton("联系客服", v -> SysConfigUtils.get().dialServerPhone(this))
                    .setNegativeButton("取消", v -> {
                    })
                    .show();
        }
    }

    @Override
    public void setDisplay(OrderCostEntity entity) {
        mPrice = TypeUtil.getValue(entity.totalFare);
        setTotalFareDisplay();
        mAdapter.setAll(entity.costItemBean);
    }

    @Override
    public void confirmFareSuccess(OrderVO vo) {
        Navigate.openOrderByConfirm(this, vo.uuid, vo);
        finish(); //关闭界面
        SpeechUtil.speech("行程结束，您可继续接单了");
    }

    @Override
    public void resetDisplay() {
        mSlideView.resetView();
    }

    @Override
    public void judgeOrderStatus(OrderVO vo) {
        if (vo == null) return;
        if (vo.subStatus != null
                && vo.subStatus != OrderStatus.ARRIVE_DEST) {
            Navigate.openOrder(this, vo.uuid, vo);
            finish();
        }
    }

    @Override
    public void showCrossCityView() {
        new CommonBottomSheetDialog.Builder(this)
                .setTitleText(getString(R.string.Tips))
                .setSubTitleText(getString(R.string.tips_order_abnormal))
                .setConfirmText(getString(R.string.know))
                .setConfirmListener(Dialog::dismiss)
                .show();
    }

    @Override
    public void showAbnormalView() {
        new CommonBottomSheetDialog.Builder(this)
                .setTitleText(getString(R.string.Tips))
                .setSubTitleText(getString(R.string.tips_order_abnormal))
                .setConfirmText(getString(R.string.know))
                .setConfirmListener(Dialog::dismiss)
                .show();
    }

    private String getStrPrice(double price) {
        return NumberUtil.getFormatValue(price);
    }

    private double highwayFare = 0; //高速费
    private double bridgeFare = 0; //过桥费
    private double parkingFare = 0; //停车费
    private double highwayFareLimit = 30;//高速费限制
    private double bridgeFareLimit = 30;//过桥费限制
    private double parkingFareLimit = 30;//停车费限制

    private void setTotalFareDisplay() {
        if (mPrice == null) {
            mTvPrice.setText("");
            return;
        }
        double totalFare = highwayFare + bridgeFare + parkingFare + mPrice;
        SpannableWrap.setText(getStrPrice(totalFare)).size(36, true)
                .textColor(ContextCompat.getColor(this, R.color.popup_item_un_choose))
                .append("元")
                .sizeSp(20, this).textColor(ContextCompat.getColor(this, R.color.popup_item_un_choose))
                .into(mTvPrice);
    }
}
