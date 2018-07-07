package com.ctkj.xj_app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ctkj.xj_app.R;
import com.ctkj.xj_app.bean.InspectRecord;
import com.ctkj.xj_app.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 昭辉 on 2018/5/10.
 */

public class InspectionRecordAdapter extends RecyclerView.Adapter<InspectionRecordAdapter.RecordViewHolder> {

    private List<InspectRecord> mRecordList = new ArrayList<>(0);
    private Context mContext;

    public InspectionRecordAdapter(Context context, List<InspectRecord> recordList) {
        this.mContext = context;
        this.mRecordList = recordList;
    }


    @Override
    public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecordViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_inspection_record, parent, false));
    }

    @Override
    public void onBindViewHolder(RecordViewHolder holder, int position) {
        InspectRecord record = mRecordList.get(position);
        holder.recordName_tv.setText(record.getName());
        holder.recordDesc_tv.setText(record.getNote());
        holder.recordStatus_tv.setText(record.getStatus());
        holder.recordTime_tv.setText(CommonUtils.getTaskOrPlanTime(record.getBuildTime().toString(),record.getFinishTime().toString()));
//        holder.recordTime_tv.setText(record.getBuildTime()+" - "+record.getFinishTime());
    }

    @Override
    public int getItemCount() {
        return mRecordList.size();
    }

    class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView recordName_tv;
        TextView recordDesc_tv;
        TextView recordStatus_tv;
        TextView recordTime_tv;

        public RecordViewHolder(View view) {
            super(view);
            recordName_tv = view.findViewById(R.id.tv_taskRecord_name);
            recordDesc_tv = view.findViewById(R.id.tv_taskRecord_description);
            recordStatus_tv = view.findViewById(R.id.tv_taskRecord_status);
            recordTime_tv = view.findViewById(R.id.tv_taskRecord_time);
        }

    }
}
