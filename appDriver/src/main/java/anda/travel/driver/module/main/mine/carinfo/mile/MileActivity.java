package anda.travel.driver.module.main.mine.carinfo.mile;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.utils.DateUtil;
import anda.travel.driver.baselibrary.utils.ToastUtil;
import anda.travel.driver.baselibrary.view.refreshview.ExRefreshView;
import anda.travel.driver.baselibrary.view.refreshview.RefreshViewListener;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.data.entity.MileEntity;
import anda.travel.driver.module.main.mine.carinfo.mile.dagger.DaggerMileComponent;
import anda.travel.driver.module.main.mine.carinfo.mile.dagger.MileModule;
import anda.travel.driver.util.FontUtils;
import anda.travel.driver.util.TimeUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MileActivity extends BaseActivity implements MileContract.View {
    private static final String CLASS_NAME = MileActivity.class.getSimpleName();

    @BindView(R2.id.tv_choice_start)
    TextView mTvChoiceStart;
    @BindView(R2.id.tv_choice_end)
    TextView mTvChoiceEnd;
    @BindView(R2.id.tv_total_mile)
    TextView mTvTotalMile;
    @BindView(R2.id.tv_sum_mile)
    TextView mTvSumMile;
    @BindView(R2.id.refresh_view)
    ExRefreshView mRefreshView;
    private MileAdapter mAdapter;

    @Inject
    MilePresenter mPresenter;
    TimePickerView mStartPickView;
    TimePickerView mEndPickView;

    private String mStartDate;
    private String mEndDate;

    private Calendar mStartCalendarBegin;//日期选择起始时间2018.1.1
    private Date mSelectStartDate;//选中的起始日期
    private Calendar mEndRangeStart;//结束日期 的 起始日期
    private Calendar mStartDefault;//起始日期选择初始值
    private Calendar mEndDefault;//结束日期选择初始值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_mile);
        ButterKnife.bind(this);

        DaggerMileComponent.builder()
                .appComponent(getAppComponent())
                .mileModule(new MileModule(this))
                .build().inject(this);

        init();
        initAdapter();
        mPresenter.reqRefreshWithLoading(TimeUtils.getTodayWithYearSplit(), TimeUtils.getTodayWithYearSplit());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.unsubscribe();
    }

    public void showMile(MileEntity entity) {
        mTvTotalMile.setText(entity.totalMileage);
        mTvSumMile.setText(entity.findSumMileage);
    }

    @Override
    public void setData(MileEntity entity) {
        mAdapter.setAll(entity.list);
        showMile(entity);
    }

    @Override
    public void addData(MileEntity entity) {
        mAdapter.addAll(entity.list);
    }

    @Override
    public void onRefreshComplete() {
        mRefreshView.setRefreshing(false);
    }

    @Override
    public void noMore() {
        mRefreshView.hasNoMoreData(true);
    }

    @Override
    public boolean isActive() {
        return false;
    }

    /**
     * 主要处理字体和日期初始化
     */
    private void init() {
        Typeface fontTypeFace = FontUtils.getFontTypeFace(this);
        mTvTotalMile.setTypeface(fontTypeFace);
        mTvSumMile.setTypeface(fontTypeFace);
        mTvChoiceStart.setText(TimeUtils.getTodayWithYearChina());
        mTvChoiceEnd.setText(TimeUtils.getTodayWithYearChina());
        mStartDate = mEndDate = TimeUtils.getTodayWithYearSplit();
        mStartCalendarBegin = Calendar.getInstance();
        mStartCalendarBegin.set(2018, 0, 1);
        mEndRangeStart = Calendar.getInstance();
        mStartDefault = Calendar.getInstance();
        mEndDefault = Calendar.getInstance();
    }

    private void initAdapter() {
        mRefreshView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MileAdapter(this);
        mRefreshView.setAdapter(mAdapter);
        mRefreshView.setRefreshListener(new RefreshViewListener() {
            @Override
            public void onRefresh() {
                mRefreshView.hasNoMoreData(false);
                mPresenter.reqRefresh(mStartDate, mEndDate);
            }

            @Override
            public void onLoadMore() {
            }
        });
        mRefreshView.setHideLoadMoreText(false);
    }

    @OnClick({R2.id.tv_choice_start, R2.id.tv_choice_end})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_choice_start) {
            mStartPickView = new TimePickerBuilder(this, (date, v) -> {
                mSelectStartDate = date;
            }).setLayoutRes(R.layout.hxyc_pickerview_custom_time, start -> {
                final TextView tvSubmit = start.findViewById(R.id.tv_finish);
                TextView tvCancel = start.findViewById(R.id.tv_cancel);
                tvSubmit.setOnClickListener(v1 -> {
                    mStartPickView.returnData();
                    mStartPickView.dismiss();
                    if (!DateUtil.compareTime(DateUtil.formatDate(mSelectStartDate, DateUtil.yyyyMMDD_SPLIT), mEndDate)) {
                        ToastUtil.getInstance().toast("起始时间不能大于结束时间");
                        return;
                    }
                    mStartDefault.setTime(mSelectStartDate);
                    mEndRangeStart.setTime(mSelectStartDate);
                    mTvChoiceStart.setText(TimeUtils.date2Str(mSelectStartDate));
                    mStartDate = DateUtil.formatDate(mSelectStartDate, DateUtil.yyyyMMDD_SPLIT);
                    mPresenter.reqRefreshWithLoading(mStartDate, mEndDate);
                });
                tvCancel.setOnClickListener(v12 -> mStartPickView.dismiss());
            })
                    .setContentTextSize(18)
                    .setType(new boolean[]{true, true, true, false, false, false})
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .setLineSpacingMultiplier(1.6f)
                    .setTextColorCenter(getResources().getColor(R.color.black))
                    .setDividerColor(getResources().getColor(R.color.line))
                    .setDate(mStartDefault)
                    .setRangDate(mStartCalendarBegin, Calendar.getInstance())
                    .build();
            mStartPickView.show();
        } else if (id == R.id.tv_choice_end) {
            mEndPickView = new TimePickerBuilder(this, (date, v) -> {
                mEndDefault.setTime(date);
                mTvChoiceEnd.setText(TimeUtils.date2Str(date));
                mEndDate = DateUtil.formatDate(date, DateUtil.yyyyMMDD_SPLIT);
            }).setLayoutRes(R.layout.hxyc_pickerview_custom_time, start -> {
                final TextView tvSubmit = start.findViewById(R.id.tv_finish);
                TextView tvCancel = start.findViewById(R.id.tv_cancel);
                tvSubmit.setOnClickListener(v1 -> {
                    mEndPickView.returnData();
                    mEndPickView.dismiss();
                    mPresenter.reqRefreshWithLoading(mStartDate, mEndDate);
                });
                tvCancel.setOnClickListener(v12 -> mEndPickView.dismiss());
            })
                    .setContentTextSize(18)
                    .setType(new boolean[]{true, true, true, false, false, false})
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .setLineSpacingMultiplier(1.6f)
                    .setTextColorCenter(getResources().getColor(R.color.black))
                    .setDividerColor(getResources().getColor(R.color.line))
                    .setDate(mEndDefault)
                    .setRangDate(mEndRangeStart, Calendar.getInstance())
                    .build();
            mEndPickView.show();
        }
    }
}
