package com.ctkj.xj_app.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ctkj.xj_app.R;
import com.ctkj.xj_app.adapter.InspectionExceptionAdapter;
import com.ctkj.xj_app.adapter.InspectionItemAdapter;
import com.ctkj.xj_app.app.base.BaseActivity;
import com.ctkj.xj_app.bean.InspectException;
import com.ctkj.xj_app.bean.InspectItem;
import com.ctkj.xj_app.util.LogUtil;
import com.hotdog.hdlibrary.core.HDToast;

import org.litepal.LitePal;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.media.ThumbnailUtils.extractThumbnail;

/**
 * created by zhaohui on 2018/5/24 8:36
 */

public class InspectionExecActivity extends BaseActivity {

    private static final String TAG = "InspectionExecActivity";

    private static final int INSPECT_EXCEPTION = 2;

    @BindView(R.id.et_result)
    EditText itemDesc_et;
    @BindView(R.id.rv_inspectException)
    RecyclerView inspectException_rv;

    private Integer leafFId;
    private ArrayList<InspectItem> fItemList = new ArrayList<>(0);
    private ArrayList<InspectException> exceptionList = new ArrayList<>(0);

    private AlertDialog dialog;
    private InspectionItemAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_exec);
        ButterKnife.bind(this);

        String title = getIntent().getStringExtra("title");
        setTitle(title);

        leafFId = getIntent().getIntExtra("leafFId", 0);
//        fItemList = getIntent().getParcelableArrayListExtra("fItemList");
        fItemList = (ArrayList<InspectItem>) LitePal.where("facilityId = ?",leafFId.toString()).find(InspectItem.class);
        LogUtil.w(TAG,fItemList.toString());

        //根据设施ID查询相关的异常项
        exceptionList = (ArrayList<InspectException>) LitePal.where("facilityId = ?",leafFId.toString()).find(InspectException.class);
        LogUtil.w(TAG,exceptionList.toString());
        inspectException_rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        inspectException_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        InspectionExceptionAdapter mAdapter = new InspectionExceptionAdapter(this, exceptionList);
        inspectException_rv.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }

    private void actionAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.inspect_item_dialogview, (ViewGroup) findViewById(R.id.ad_item));
        RecyclerView itemList_rv = layout.findViewById(R.id.rv_inspectionItem);
        itemList_rv.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        itemList_rv.setLayoutManager(lm);
        mAdapter = new InspectionItemAdapter(this,fItemList);
        itemList_rv.setAdapter(mAdapter);

        mAdapter.setClickListener(new InspectionItemAdapter.onInspectItemClickListener() {
            @Override
            public void onItemClick(InspectItem item) {
                Intent intent = new Intent(InspectionExecActivity.this, InspectionExceptionActivity.class);
                intent.putExtra("leafFId", leafFId);
//                intent.putExtra("itemName", item.getName());
                intent.putExtra("itemId", item.getItemId());
                startActivityForResult(intent,INSPECT_EXCEPTION);
            }
        });

        builder.setView(layout);
        dialog = builder.create();
        dialog.show();

    }


    @OnClick(R.id.iv_addItem)
    public void onAddItemClick(View view){
        actionAlertDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case INSPECT_EXCEPTION:
                if (resultCode == RESULT_OK) {
                    dialog.dismiss();
                    exceptionList = (ArrayList<InspectException>) LitePal.where("facilityId = ?",leafFId.toString()).find(InspectException.class);
                    InspectionExceptionAdapter mAdapter = new InspectionExceptionAdapter(this, exceptionList);
                    inspectException_rv.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.btn_commit)
    public void onSaveBtnClick(View view) {
        Intent intent = new Intent();
        intent.putExtra("dataReturn", leafFId);
//        intent.putParcelableArrayListExtra("exceptionList", exceptionList);
        setResult(RESULT_OK, intent);
        HDToast.show("提交成功", this);
        finish();
    }


}
