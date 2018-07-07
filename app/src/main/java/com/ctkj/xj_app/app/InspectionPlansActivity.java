package com.ctkj.xj_app.app;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ctkj.xj_app.R;
import com.ctkj.xj_app.adapter.InspectionPlanAdapter;
import com.ctkj.xj_app.app.base.BaseActivity;
import com.ctkj.xj_app.bean.InspectPlan;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InspectionPlansActivity extends BaseActivity {

    private static final String TAG = "InspectionPlansActivity";

    @BindView(R.id.rv_inspectionPlan)
    RecyclerView inspectionPlan_rv;

    InspectionPlanAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_plans);
        ButterKnife.bind(this);

        ArrayList<InspectPlan> plans = getIntent().getParcelableArrayListExtra("planList");
        setTitle(getIntent().getStringExtra("title"));
        inspectionPlan_rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        inspectionPlan_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new InspectionPlanAdapter(this, plans);
        inspectionPlan_rv.setAdapter(mAdapter);
    }
}
