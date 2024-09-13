package anda.travel.driver.module.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.view.HeadView;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.module.task.dagger.DaggerTaskListComponent;
import anda.travel.driver.module.task.dagger.TaskListModule;
import anda.travel.driver.module.vo.TaskListVO;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 功能描述：任务中心(列表)
 */
@SuppressLint("NonConstantResourceId")
public class TaskListActivity extends BaseActivity implements TaskListContract.View {

    @BindView(R2.id.head_view)
    HeadView mHeadView;
    @BindView(R2.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R2.id.tv_empty)
    TextView mTvEmpty;

    private TaskListAdapter mAdapter;

    @Inject
    TaskListPresenter mPresenter;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, TaskListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_task_list);
        ButterKnife.bind(this);
        DaggerTaskListComponent.builder()
                .appComponent(getAppComponent())
                .taskListModule(new TaskListModule(this))
                .build().inject(this);

        mAdapter = new TaskListAdapter(this);
        mAdapter.addEmptyLayout(mTvEmpty); //设置列表为空时显示的控件
        mAdapter.setOnClickListener(R.id.layout_item, (position, view, item) -> {
            // 跳转到任务详情页
            TaskDetailActivity.actionStart(this, item.getUuid());
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mPresenter.reqTaskList(); //获取任务列表
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
    public void showTaskList(List<TaskListVO> list) {
        mAdapter.setAll(list);
    }

}
