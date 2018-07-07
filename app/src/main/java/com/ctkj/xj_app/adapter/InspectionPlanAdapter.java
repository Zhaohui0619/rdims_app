package com.ctkj.xj_app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ctkj.xj_app.R;
import com.ctkj.xj_app.bean.InspectPlan;
import com.ctkj.xj_app.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 昭辉 on 2018/5/10.
 */

public class InspectionPlanAdapter extends RecyclerView.Adapter<InspectionPlanAdapter.PlanViewHolder> {

    private List<InspectPlan> mPlanList = new ArrayList<>(0);
    private Context mContext;

    public InspectionPlanAdapter(Context context, List<InspectPlan> planList) {
        this.mContext = context;
        this.mPlanList = planList;
    }


    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PlanViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_inspection_plan, parent, false));
    }

    @Override
    public void onBindViewHolder(PlanViewHolder holder, int position) {
        InspectPlan plan = mPlanList.get(position);
        holder.planName_tv.setText(plan.getName());
        holder.planType_tv.setText("("+plan.getType()+")");
        holder.planDesc_tv.setText(plan.getNote());
        holder.planStatus_tv.setText(plan.getStatus());
        holder.planTime_tv.setText(CommonUtils.getTaskOrPlanTime(plan.getBeginDate().toString(),plan.getEndDate().toString()));
//        holder.planTime_tv.setText(plan.getBeginDate()+" - "+plan.getEndDate());
    }

    @Override
    public int getItemCount() {
        return mPlanList.size();
    }

    class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView planName_tv;
        TextView planType_tv;
        TextView planDesc_tv;
        TextView planStatus_tv;
        TextView planTime_tv;

        public PlanViewHolder(View view) {
            super(view);
            planName_tv = view.findViewById(R.id.tv_plan_name);
            planType_tv = view.findViewById(R.id.tv_plan_type);
            planDesc_tv = view.findViewById(R.id.tv_plan_description);
            planStatus_tv = view.findViewById(R.id.tv_plan_status);
            planTime_tv = view.findViewById(R.id.tv_plan_time);
        }

    }
}
