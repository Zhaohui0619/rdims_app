package com.ctkj.xj_app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.ctkj.xj_app.R;
import com.ctkj.xj_app.bean.FacilityNode;
import com.multilevel.treelist.Node;
import com.multilevel.treelist.TreeHelper;
import com.multilevel.treelist.TreeRecyclerAdapter;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SimpleTreeRecyclerAdapter extends TreeRecyclerAdapter {

    private onTreeRecyclerItemClickListener clickListener = null;

    public SimpleTreeRecyclerAdapter(RecyclerView mTree, Context context, List<Node> datas, int defaultExpandLevel, int iconExpand, int iconNoExpand) {
        super(mTree, context, datas, defaultExpandLevel, iconExpand, iconNoExpand);
    }

    public SimpleTreeRecyclerAdapter(RecyclerView mTree, Context context, List<Node> datas, int defaultExpandLevel) {
        super(mTree, context, datas, defaultExpandLevel);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FacilityHolder(View.inflate(mContext, R.layout.item_inspection_facility, null));
    }

//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        super.onBindViewHolder(holder, position);
//    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        FacilityHolder mHolder = (FacilityHolder) holder;
        final Node node = mNodes.get(position);
//        convertView = getConvertView(node, position, convertView, parent);
        // 设置内边距
        View itemView = holder.itemView;
        itemView.setPadding(node.getLevel() * 30, 3, 3, 3);
        FacilityNode fNode = LitePal.where("childId = ?",node.getId().toString()).find(FacilityNode.class).get(0);
        if (node.isParentExpand() && fNode.getStatus()== -1){
            mHolder.label.setTextColor(Color.RED);
        }else if(node.isParentExpand() && fNode.getStatus() == 1){
            mHolder.label.setTextColor(Color.GREEN);
        }else {
            mHolder.label.setTextColor(Color.BLACK);
        }
/*        if (node.isParentExpand() && node.getName().contains("已巡检")) {
            mHolder.label.setTextColor(Color.RED);
        }else {
            mHolder.label.setTextColor(Color.BLACK);
        }*/

        /**
         * 设置节点点击时，可以展开以及关闭,将事件继续往外公布
         */
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandOrCollapse(position);
                if (clickListener != null) {
                    clickListener.onItemClick(node);
                }
            }
        });

        onBindViewHolder(node, holder, position);
    }

    @Override
    public void onBindViewHolder(final Node node, RecyclerView.ViewHolder holder, int position) {

        final FacilityHolder viewHolder = (FacilityHolder) holder;
        //todo do something

        if (node.getIcon() == -1) {
            viewHolder.icon.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.icon.setVisibility(View.VISIBLE);
            viewHolder.icon.setImageResource(node.getIcon());
        }

        viewHolder.label.setText(node.getName());


    }

    static class FacilityHolder extends RecyclerView.ViewHolder {

        public TextView label;

        public ImageView icon;

        public FacilityHolder(View itemView) {
            super(itemView);

            label = itemView.findViewById(R.id.tv_facilityItem);
            icon = itemView.findViewById(R.id.iv_facilityIcon);

        }

    }

    private void expandOrCollapse(int position) {
        Node n = mNodes.get(position);

        if (n != null) {// 排除传入参数错误异常
            if (!n.isLeaf()) {
                n.setExpand(!n.isExpand());
                mNodes = TreeHelper.filterVisibleNode(mAllNodes);
                notifyDataSetChanged();// 刷新视图
            }//else
//            {
//                Intent intent = new Intent(mContext,InspectionExecActivity.class);
//                intent.putExtra("title",n.getName());
//                intent.putExtra("itemId",Integer.parseInt(n.getTaskId().toString()));
//
//                mActivity = (AppCompatActivity) mContext;
//                mActivity.startActivityForResult(intent,1);
//            }
        }
    }

    public interface onTreeRecyclerItemClickListener {
        void onItemClick(Node node);
    }

    public void setClickListener(onTreeRecyclerItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

}
