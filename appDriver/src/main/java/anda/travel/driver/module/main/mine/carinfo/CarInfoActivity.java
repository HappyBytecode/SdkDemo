package anda.travel.driver.module.main.mine.carinfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.utils.DateUtil;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.data.entity.CarInfoEntity;
import anda.travel.driver.module.main.mine.carinfo.dagger.CarInfoModule;
import anda.travel.driver.module.main.mine.carinfo.dagger.DaggerCarInfoComponent;
import anda.travel.driver.module.main.mine.carinfo.mile.MileActivity;
import anda.travel.driver.util.FontUtils;
import anda.travel.driver.widget.DividerDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class CarInfoActivity extends BaseActivity implements CarInfoContract.View {

    @BindView(R2.id.img_head_left)
    ImageView mBack;
    @BindView(R2.id.tv_title)
    TextView mTitle;
    @BindView(R2.id.tv_mile_value)
    TextView mMile;
    @BindView(R2.id.tv_charge_value)
    TextView mCharge;
    @BindView(R2.id.tv_update)
    TextView mUpdate;
    @BindView(R2.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R2.id.tv_vin)
    TextView mVin;

    private CarInfoAdapter mAdapter;

    @Inject
    CarInfoPresenter mPresenter;

    public static void actionStart(Context context, String title) {
        Intent intent = new Intent(context, CarInfoActivity.class);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_car_info);
        ButterKnife.bind(this);
        DaggerCarInfoComponent.builder()
                .appComponent(getAppComponent())
                .carInfoModule(new CarInfoModule(this))
                .build().inject(this);
        init();
        mPresenter.reqCarInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    private void init() {
        Typeface fontTypeFace = FontUtils.getFontTypeFace(this);
        mMile.setTypeface(fontTypeFace);
        mCharge.setTypeface(fontTypeFace);
        mTitle.setText(getIntent().getStringExtra("title"));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter = new CarInfoAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((position, view, item) -> {
            if (item.key.equals("累计里程")) {
                CarInfoActivity.this.startActivity(new Intent(CarInfoActivity.this, MileActivity.class));
            }
        });
    }

    @Override
    public void setData(CarInfoEntity entity) {
        if (entity == null) {
            return;
        }
        mMile.setText(entity.todayMile);
        mCharge.setText(entity.remainCharge);
        mUpdate.setText(new StringBuilder().append("上次更新：").append(DateUtil.translateDate(DateUtil.parseLong(entity.updateTime), System.currentTimeMillis())).toString());
        mVin.setText(new StringBuilder().append("车辆识别码（VIN码）:").append("\n").append(entity.vin).toString());
        mAdapter.setAll(entity.items);
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @OnClick({R2.id.img_head_left, R2.id.car_update_status})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_head_left) {
            finish();
        } else if (id == R.id.car_update_status) {
            mPresenter.reqCarInfo();
        }
    }
}
