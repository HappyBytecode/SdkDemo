package anda.travel.driver.module.task;

import android.content.Context;

import java.util.ArrayList;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.adapter.SuperAdapter;
import anda.travel.driver.baselibrary.adapter.internal.SuperViewHolder;
import anda.travel.driver.module.vo.TaskListVO;

class TaskListAdapter extends SuperAdapter<TaskListVO> {

    public TaskListAdapter(Context context) {
        super(context, new ArrayList<>(), R.layout.hxyc_item_task_list);
    }

    @Override
    public void onBind(SuperViewHolder holder, int viewType, int position, TaskListVO item) {
        holder.setText(R.id.tv_title, item.getTitleStr())
                .setText(R.id.tv_duration, item.getDurationStr())
                .setText(R.id.tv_status, item.getStatusStr());
    }

}
