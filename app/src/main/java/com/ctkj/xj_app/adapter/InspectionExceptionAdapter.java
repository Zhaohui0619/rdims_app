package com.ctkj.xj_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.ctkj.xj_app.MyApplication;
import com.ctkj.xj_app.R;
import com.ctkj.xj_app.app.ExceptionImagesActivity;
import com.ctkj.xj_app.app.InspectionExecActivity;
import com.ctkj.xj_app.bean.InspectException;
import com.ctkj.xj_app.util.LogUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 昭辉 on 2018/5/23.
 */

public class InspectionExceptionAdapter extends RecyclerView.Adapter<InspectionExceptionAdapter.ExceptionViewHolder> {

    private static final String TAG = "InspectionExceptionAdapter";

    private AlertDialog mDialog;

    private List<InspectException> mExceptionList = new ArrayList<>(0);
    private Context mContext;

    public InspectionExceptionAdapter(Context context, List<InspectException> exceptionList){
        this.mContext = context;
        this.mExceptionList = exceptionList;
    }

    @Override
    public ExceptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExceptionViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_inspection_exception, parent, false));
    }

    @Override
    public void onBindViewHolder(ExceptionViewHolder holder, int position) {
        final InspectException exception = mExceptionList.get(position);
        holder.exceptionName_tv.setText(exception.getItemName());
        holder.exceptionDesc_tv.setText(exception.getInspectDescription());
        holder.exceptionImage_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(view.getContext(), ExceptionImagesActivity.class);
//                LogUtil.w(TAG,exception.getInspectMultimediaList());
//                LogUtil.w(TAG, LitePal.where("itemId = ?",exception.getItemId().toString()).find(InspectException.class).get(0).toString());
//                intent.putExtra("imagePathList",exception.getInspectMultimediaList());
//                view.getContext().startActivity(intent);
            actionAlertDialog(view,exception.getInspectMultimediaList());

            }
        });
        holder.exceptionName_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), InspectionExecActivity.class);
                intent.putExtra("itemId",exception.getItemId());
                intent.putExtra("leafFId",exception.getFacilityId());
                view.getContext().startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mExceptionList.size();
    }

    class ExceptionViewHolder extends RecyclerView.ViewHolder{
        TextView exceptionName_tv;
        TextView exceptionDesc_tv;
        TextView exceptionImage_tv;

        public ExceptionViewHolder(View itemView) {
            super(itemView);
            exceptionName_tv = itemView.findViewById(R.id.tv_exception_name);
            exceptionDesc_tv = itemView.findViewById(R.id.tv_exception_desc);
            exceptionImage_tv = itemView.findViewById(R.id.tv_exception_image);
        }
    }

    private void actionAlertDialog(View view,String imagePath){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater) MyApplication.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.activity_exception_images,(ViewGroup)view.findViewById(R.id.ad_images));
        GridView mGridView =layout.findViewById(R.id.gv_exception);
        ExceptionImagesAdapter mAdapter = new ExceptionImagesAdapter(getImagePaths(imagePath),mContext);
        mGridView.setAdapter(mAdapter);
        builder.setView(layout);
        mDialog = builder.create();
        mDialog.show();
    }

    private List<String> getImagePaths(String path) {
        List<String> pathList = new ArrayList<>(0);
        String[] p = path.split(",");
        for (String s : p) {
            if (s.contains(".jpg") || s.contains(".png") || s.contains(".gif")) {
                pathList.add(s);
            }
        }
        return pathList;
    }
}
