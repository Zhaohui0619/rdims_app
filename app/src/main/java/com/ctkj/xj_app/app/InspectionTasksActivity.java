package com.ctkj.xj_app.app;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ctkj.xj_app.R;
import com.ctkj.xj_app.adapter.InspectionTaskAdapter;
import com.ctkj.xj_app.app.base.BaseActivity;
import com.ctkj.xj_app.bean.InspectTask;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InspectionTasksActivity extends BaseActivity {

    private static final String TAG = "InspectionTasksActivity";

    @BindView(R.id.rv_inspectionTask)
    RecyclerView inspectionTask_rv;

    InspectionTaskAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_tasks);
        ButterKnife.bind(this);

        ArrayList<InspectTask> tasks = getIntent().getParcelableArrayListExtra("taskList");
        setTitle(getIntent().getStringExtra("title"));
        inspectionTask_rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        inspectionTask_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new InspectionTaskAdapter(this, tasks);
        inspectionTask_rv.setAdapter(mAdapter);

    }
}
