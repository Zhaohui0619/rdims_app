package com.ctkj.xj_app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ctkj.xj_app.R;
import com.ctkj.xj_app.bean.InspectTask;
import com.ctkj.xj_app.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 昭辉 on 2018/5/10.
 */

public class InspectionTaskAdapter extends RecyclerView.Adapter<InspectionTaskAdapter.TaskViewHolder> {

    private List<InspectTask> mTaskList = new ArrayList<>(0);
    private Context mContext;

    public InspectionTaskAdapter(Context context, List<InspectTask> taskList) {
        this.mContext = context;
        this.mTaskList = taskList;
    }


    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TaskViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_inspection_task, parent, false));
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        final InspectTask task = mTaskList.get(position);
        holder.taskName_tv.setText(task.getName());
        holder.taskDesc_tv.setText(task.getNote());
        holder.taskStatus_tv.setText(task.getStatus());
        holder.taskTime_tv.setText(CommonUtils.getTaskOrPlanTime(task.getBuildTime().toString(),task.getFinishTime().toString()));
    }

    @Override
    public int getItemCount() {
        return mTaskList.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskName_tv;
        TextView taskDesc_tv;
        TextView taskStatus_tv;
        TextView taskTime_tv;
//        Button taskCommit_btn;

        public TaskViewHolder(View view) {
            super(view);
            taskName_tv = view.findViewById(R.id.tv_task_name);
            taskDesc_tv = view.findViewById(R.id.tv_task_description);
            taskStatus_tv = view.findViewById(R.id.tv_task_status);
            taskTime_tv = view.findViewById(R.id.tv_task_time);
//            taskCommit_btn = view.findViewById(R.id.btn_commit_task);
        }

    }
}
