package anda.travel.driver.module.main.mine.walletnew.balancedetail;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.baselibrary.utils.DateUtil;
import anda.travel.driver.baselibrary.view.refreshview.RefreshViewListener;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.data.entity.BalanceDetailListEntity;
import anda.travel.driver.databinding.HxycActivityBalanceDetailBinding;
import anda.travel.driver.module.main.mine.walletnew.balancedetail.dagger.BalanceDetailModule;
import anda.travel.driver.module.main.mine.walletnew.balancedetail.dagger.DaggerBalanceDetailComponent;
import anda.travel.driver.util.TimeUtils;

public class BalanceDetailActivity extends BaseActivity implements BalanceDetailContract.View {
    private HxycActivityBalanceDetailBinding binding;
    TimePickerView mTimePickView;

    private Date mSelectDate;
    private Calendar mDefaultDate;
    private Calendar mCalendarBegin;

    private BalanceDetailAdapter mAdapter;

    private boolean mIsShowNetError;//是否显示了网络异常界面
    private boolean mIsNetErrorInflate;//neterrorview是否导入

    @Inject
    BalanceDetailPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = HxycActivityBalanceDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        DaggerBalanceDetailComponent.builder()
                .appComponent(HxClientManager.getAppComponent())
                .balanceDetailModule(new BalanceDetailModule(this))
                .build().inject(this);
        initView();
        initAdapter();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.unsubscribe();
    }

    private void initView() {
        mCalendarBegin = Calendar.getInstance();
        mCalendarBegin.set(2018, 0, 1);
        mDefaultDate = Calendar.getInstance();
        binding.tvChoiceStart.setText(TimeUtils.getTodayWithYearChinaButDay());
        mPresenter.setStartDate(DateUtil.formatDate(new Date(), DateUtil.yyyyMM));
        binding.tab.addTab("可提现金额");
        binding.tab.addTab("待入账金额");
        binding.tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    mPresenter.setStatus(1);
                } else if (1 == tab.getPosition()) {
                    mPresenter.setStatus(0);
                }
                binding.exRefreshView.onRefresh();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.exRefreshView.setLayoutManager(linearLayoutManager);
        mAdapter = new BalanceDetailAdapter(this);
        mAdapter.addEmptyLayout(binding.tvEmpty);
        binding.exRefreshView.setAdapter(mAdapter);
        binding.exRefreshView.setRefreshListener(new RefreshViewListener() {
            @Override
            public void onRefresh() {
                hideNoMore();
                mPresenter.reqRefresh();
            }

            @Override
            public void onLoadMore() {
                mPresenter.reqMore();
            }
        });
        binding.exRefreshView.setHideLoadMoreText(false);
        binding.exRefreshView.initFoodView();
        mPresenter.reqRefresh();
    }

    @Override
    public void setData(List<BalanceDetailListEntity> balanceDetailListEntityList) {
        mAdapter.setAll(balanceDetailListEntityList);
        if (balanceDetailListEntityList.size() > 0) {
            binding.exRefreshView.initFoodView();
        } else {
            binding.exRefreshView.hideFoodView();
        }
    }

    @Override
    public void addData(List<BalanceDetailListEntity> balanceDetailListEntityList) {
        mAdapter.addAll(balanceDetailListEntityList);
        if (balanceDetailListEntityList.size() > 0) {
            binding.exRefreshView.initFoodView();
        } else {
            binding.exRefreshView.hideFoodView();
        }
    }

    @Override
    public void onRefreshComplete() {
        binding.exRefreshView.setRefreshing(false);
    }

    @Override
    public void noMore() {
        binding.exRefreshView.hasNoMoreData(true);
    }

    @Override
    public void hideNoMore() {
        binding.exRefreshView.hasNoMoreData(false);
    }

    @Override
    public void netErrorView(boolean isShow) {
        if (isShow && !mIsShowNetError) {
            if (binding.tvEmpty.getVisibility() == View.VISIBLE) {
                //如果空界面显示的情况下，需要隐藏空界面，再去显示网络异常界面，否则会重叠
                mAdapter.emptyGone();
            }
            //需要显示但是实际没有显示网络错误页面的时候，需要去显示网络错误页
            if (!mIsNetErrorInflate) {
                mIsNetErrorInflate = true;
                View netErrorView = binding.vsNetError.inflate();//这里相当于把mNetErrorView显示
                binding.exRefreshView.setVisibility(View.GONE);
                mIsShowNetError = true;
                netErrorView.findViewById(R.id.tv_reload).setOnClickListener(v -> mPresenter.reload());
            } else {
                binding.vsNetError.setVisibility(View.VISIBLE);
                binding.exRefreshView.setVisibility(View.GONE);
                mIsShowNetError = true;
            }
        } else if (!isShow && mIsShowNetError) {
            //不需要显示但是实际已经显示网络错误页的时候，需要隐藏网络错误页
            binding.vsNetError.setVisibility(View.GONE);
            binding.exRefreshView.setVisibility(View.VISIBLE);
            mIsShowNetError = false;
        }
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        if (isBtnBuffering()) {
            return;
        }
        if (view.getId() == R.id.tv_choice_start) {
            mTimePickView = new TimePickerBuilder(this, (date, v) -> mSelectDate = date).setLayoutRes(R.layout.hxyc_pickerview_custom_time, start -> {
                final TextView tvSubmit = start.findViewById(R.id.tv_finish);
                TextView tvCancel = start.findViewById(R.id.tv_cancel);
                tvSubmit.setOnClickListener(v1 -> {
                    mTimePickView.returnData();
                    mTimePickView.dismiss();
                    mDefaultDate.setTime(mSelectDate);
                    binding.tvChoiceStart.setText(TimeUtils.date2StrButDay(mSelectDate));
                    mPresenter.setStartDate(DateUtil.formatDate(mSelectDate, DateUtil.yyyyMM));
                    binding.exRefreshView.onRefresh();
                });
                tvCancel.setOnClickListener(v12 -> mTimePickView.dismiss());
            })
                    .setContentTextSize(18)
                    .setType(new boolean[]{true, true, false, false, false, false}).isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .setLineSpacingMultiplier(1.6f)
                    .setTextColorCenter(getResources().getColor(R.color.black))
                    .setDividerColor(getResources().getColor(R.color.line))
                    .setDate(mDefaultDate)
                    .setRangDate(mCalendarBegin, Calendar.getInstance())
                    .build();
            mTimePickView.show();
        }
    }
}