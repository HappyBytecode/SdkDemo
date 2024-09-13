package anda.travel.driver.module.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.view.HeadView;
import anda.travel.driver.common.BaseActivityWithoutIconics;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.module.task.dagger.DaggerTaskDetailComponent;
import anda.travel.driver.module.task.dagger.TaskDetailModule;
import anda.travel.driver.module.vo.TaskDetailVO;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 功能描述：任务详情
 */
@SuppressLint("NonConstantResourceId")
public class TaskDetailActivity extends BaseActivityWithoutIconics implements TaskDetailContract.View {

    @BindView(R2.id.head_view)
    HeadView mHeadView;
    @BindView(R2.id.tv_name)
    TextView mTvName;
    @BindView(R2.id.tv_status)
    TextView mTvStatus;
    @BindView(R2.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R2.id.tv_progress)
    TextView mTvProgress;
    @BindView(R2.id.tv_date)
    TextView mTvDate;
    @BindView(R2.id.tv_time)
    TextView mTvTime;
    @BindView(R2.id.tv_require)
    TextView mTvRequire;
    @BindView(R2.id.tv_award)
    TextView mTvAward;
    @BindView(R2.id.tv_describe)
    TextView mTvDescribe;

    @Inject
    TaskDetailPresenter mPresenter;

    public static void actionStart(Context context, String taskUuid) {
        Intent intent = new Intent(context, TaskDetailActivity.class);
        intent.putExtra(IConstants.TASK_UUID, taskUuid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_task_detail);
        ButterKnife.bind(this);
        DaggerTaskDetailComponent.builder()
                .appComponent(getAppComponent())
                .taskDetailModule(new TaskDetailModule(this))
                .build().inject(this);
        mPresenter.onCreate(getIntent().getStringExtra(IConstants.TASK_UUID));
    }

    @Override
    public boolean isActive() {
        return false;
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
    public void showTaskDetail(TaskDetailVO vo) {
        if (vo == null) return;
        mTvName.setText(vo.getTaskName());
        mTvStatus.setText(vo.getStatusStr());
        mProgressBar.setMax(vo.getMax());
        mProgressBar.setProgress(vo.getProgress());
        mTvProgress.setText(vo.getProgressStr());
        mTvDate.setText(vo.getDuration());
        mTvTime.setText(vo.getDurations());
        mTvRequire.setText(vo.getRequiresStr());
        mTvAward.setText(vo.getContent());
        mTvDescribe.setText(vo.getDescription());
    }
}