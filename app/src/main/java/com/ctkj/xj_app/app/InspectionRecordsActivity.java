package com.ctkj.xj_app.app;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ctkj.xj_app.R;
import com.ctkj.xj_app.adapter.InspectionRecordAdapter;
import com.ctkj.xj_app.app.base.BaseActivity;
import com.ctkj.xj_app.bean.InspectRecord;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InspectionRecordsActivity extends BaseActivity {

    private static final String TAG = "InspectionRecordsActivity";

    @BindView(R.id.rv_inspectionRecord)
    RecyclerView inspectionRecord_rv;

    InspectionRecordAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_records);
        ButterKnife.bind(this);

        ArrayList<InspectRecord> records = getIntent().getParcelableArrayListExtra("recordList");
        setTitle(getIntent().getStringExtra("title"));
        inspectionRecord_rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        inspectionRecord_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new InspectionRecordAdapter(this, records);
        inspectionRecord_rv.setAdapter(mAdapter);
    }
}
