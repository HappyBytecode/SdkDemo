package anda.travel.driver.module.main.mine.journal;

import android.annotation.SuppressLint;
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
import anda.travel.driver.baselibrary.utils.NumberUtil;
import anda.travel.driver.baselibrary.utils.ToastUtil;
import anda.travel.driver.baselibrary.view.HeadView;
import anda.travel.driver.baselibrary.view.refreshview.ExRefreshView;
import anda.travel.driver.baselibrary.view.refreshview.RefreshViewListener;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.data.entity.JournalEntity;
import anda.travel.driver.module.main.mine.journal.dagger.DaggerJournalComponent;
import anda.travel.driver.module.main.mine.journal.dagger.JournalModule;
import anda.travel.driver.util.FontUtils;
import anda.travel.driver.util.TimeUtils;
import anda.travel.driver.widget.layout.BaseLinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class JournalActivity extends BaseActivity implements JournalContract.View {

    @BindView(R2.id.head_view)
    HeadView mHeadView;
    @BindView(R2.id.tv_choice_start)
    TextView mTvChoiceStart;
    @BindView(R2.id.tv_choice_end)
    TextView mTvChoiceEnd;
    @BindView(R2.id.journal_baselayout)
    BaseLinearLayout mBaseLayout;
    @BindView(R2.id.tv_total_income)
    TextView mTvTotalIncome;
    @BindView(R2.id.tv_basic_fare)
    TextView mTvBasicFare;
    @BindView(R2.id.tv_award_fare)
    TextView mTvAwardFare;
    @BindView(R2.id.tv_order_count)
    TextView mOrderCount;
    @BindView(R2.id.refresh_view)
    ExRefreshView mRefreshView;
    private JournalAdapter mAdapter;

    @Inject
    JournalPresenter mPresenter;
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
        setContentView(R.layout.hxyc_activity_journal);
        ButterKnife.bind(this);

        DaggerJournalComponent.builder()
                .appComponent(getAppComponent())
                .journalModule(new JournalModule(this))
                .build().inject(this);

        init();
        initAdapter();

        mPresenter.reqRefresh(TimeUtils.getTodayWithYearNoSplit(), TimeUtils.getTodayWithYearNoSplit());
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

    public void showJournal(JournalEntity entity) {
        mOrderCount.setText(getString(R.string.journal_total_order, entity.number));
        mTvTotalIncome.setText(NumberUtil.getFormatValue(entity.totalIncome + entity.rewardIncome));
        mTvBasicFare.setText(getString(R.string.journal_money, NumberUtil.getFormatValue((entity.totalIncome))));
        mTvAwardFare.setText(getString(R.string.journal_money, NumberUtil.getFormatValue(entity.rewardIncome)));
    }

    @Override
    public void setData(JournalEntity entity) {
        mAdapter.setAll(entity.details);
        showJournal(entity);
    }

    @Override
    public void addData(JournalEntity entity) {
        mAdapter.addAll(entity.details);
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
    public void hideNoMore() {
        mRefreshView.hasNoMoreData(false);
    }

    @Override
    public boolean isActive() {
        return false;
    }

    /**
     * 主要处理字体和日期初始化
     */
    private void init() {
        mBaseLayout.setRadiusAndShadow(20, 16);
        Typeface fontTypeFace = FontUtils.getFontTypeFace(this);
        mTvTotalIncome.setTypeface(fontTypeFace);
        mTvChoiceStart.setText(TimeUtils.getTodayWithYearChina());
        mTvChoiceEnd.setText(TimeUtils.getTodayWithYearChina());
        mStartDate = mEndDate = TimeUtils.getTodayWithYearNoSplit();
        mStartCalendarBegin = Calendar.getInstance();
        mStartCalendarBegin.set(2018, 0, 1);
        mEndRangeStart = Calendar.getInstance();
        mStartDefault = Calendar.getInstance();
        mEndDefault = Calendar.getInstance();
    }

    private void initAdapter() {
        mRefreshView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new JournalAdapter(this);
        mRefreshView.setAdapter(mAdapter);
        mRefreshView.setRefreshListener(new RefreshViewListener() {
            @Override
            public void onRefresh() {
                hideNoMore();
                mPresenter.reqRefresh(mStartDate, mEndDate);
            }

            @Override
            public void onLoadMore() {
                mPresenter.reqMore(mStartDate, mEndDate);
            }
        });
        mRefreshView.setHideLoadMoreText(false);
    }

    @OnClick({R2.id.tv_choice_start, R2.id.tv_choice_end})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_choice_start) {
            mStartPickView = new TimePickerBuilder(this, (date, v) -> mSelectStartDate = date).setLayoutRes(R.layout.hxyc_pickerview_custom_time, start -> {
                final TextView tvSubmit = start.findViewById(R.id.tv_finish);
                TextView tvCancel = start.findViewById(R.id.tv_cancel);
                tvSubmit.setOnClickListener(v1 -> {
                    mStartPickView.returnData();
                    mStartPickView.dismiss();
                    if (!DateUtil.compareTime(DateUtil.formatDate(mSelectStartDate, DateUtil.yyyyMMDD), mEndDate)) {
                        ToastUtil.getInstance().toast("起始时间不能大于结束时间");
                        return;
                    }
                    mStartDefault.setTime(mSelectStartDate);
                    mEndRangeStart.setTime(mSelectStartDate);
                    mTvChoiceStart.setText(TimeUtils.date2Str(mSelectStartDate));
                    mStartDate = DateUtil.formatDate(mSelectStartDate, DateUtil.yyyyMMDD);
                    hideNoMore();
                    mRefreshView.hideFoodView();
                    mPresenter.reqRefresh(mStartDate, mEndDate);
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
                mEndDate = DateUtil.formatDate(date, DateUtil.yyyyMMDD);
            }).setLayoutRes(R.layout.hxyc_pickerview_custom_time, start -> {
                final TextView tvSubmit = start.findViewById(R.id.tv_finish);
                TextView tvCancel = start.findViewById(R.id.tv_cancel);
                tvSubmit.setOnClickListener(v1 -> {
                    mEndPickView.returnData();
                    mEndPickView.dismiss();
                    hideNoMore();
                    mRefreshView.hideFoodView();
                    mPresenter.reqRefresh(mStartDate, mEndDate);
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
