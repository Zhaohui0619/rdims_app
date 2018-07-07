package com.ctkj.xj_app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ctkj.xj_app.R;
import com.ctkj.xj_app.bean.InspectItem;

import java.util.ArrayList;
import java.util.List;

/**
 * created by zhaohui on 2018/6/4 17:29
 */

public class InspectionItemAdapter extends RecyclerView.Adapter<InspectionItemAdapter.ItemViewHolder> {

    private onInspectItemClickListener mClickListener = null;

    private List<InspectItem> mItemList = new ArrayList<>(0);
    private Context mContext;

    public InspectionItemAdapter(Context context,List<InspectItem> itemList){
        this.mContext = context;
        this.mItemList = itemList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_inspection_item,parent,false));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        final InspectItem item = mItemList.get(position);
        holder.tv_itemName.setText(item.getName());
        holder.tv_itemName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener!=null){
                    mClickListener.onItemClick(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{

        TextView tv_itemName;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tv_itemName = itemView.findViewById(R.id.tv_item_name);
        }
    }

    public interface onInspectItemClickListener {
        void onItemClick(InspectItem item);
    }

    public void setClickListener(onInspectItemClickListener clickListener) {
        this.mClickListener = clickListener;
    }

}

