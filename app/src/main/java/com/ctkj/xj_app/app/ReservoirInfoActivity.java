package com.ctkj.xj_app.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ctkj.xj_app.R;
import com.ctkj.xj_app.app.base.BaseActivity;
import com.ctkj.xj_app.bean.Reservoir;
import com.ctkj.xj_app.bean.ReservoirInfo;
import com.ctkj.xj_app.util.LogUtil;
import com.ctkj.xj_app.widget.TitleInfoView;
import com.google.gson.Gson;

import org.litepal.LitePal;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReservoirInfoActivity extends BaseActivity {

    private static final String TAG = "ReservoirInfoActivity";

    @BindView(R.id.tv_reservoirName)
    TitleInfoView tv_reservoirName;
    @BindView(R.id.tv_reservoirType)
    TitleInfoView tv_reservoirType;
    @BindView(R.id.tv_reservoirDesc)
    TitleInfoView tv_reservoirDesc;

    private ReservoirInfo reservoirInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservoir_info);
        ButterKnife.bind(this);

        Integer reservoirId = getIntent().getIntExtra("reservoirId",0);
        reservoirInfo = LitePal.where("reservoirId = ?",reservoirId.toString()).find(ReservoirInfo.class).get(0);
//        reservoirInfo = getIntent().getParcelableExtra("reservoirInfo");
        setTitle(reservoirInfo.getName());

        initWidgets();
        setListeners();

    }

    /**
     * 初始化控件
     */
    private void initWidgets() {
        tv_reservoirName.setTitle("名称");
        tv_reservoirName.setInfo(reservoirInfo.getName());
        tv_reservoirType.setTitle("类型");
        tv_reservoirType.setInfo(reservoirInfo.getDegree());
        tv_reservoirDesc.setTitle("简介");
        tv_reservoirDesc.setInfo(reservoirInfo.getDescription());
    }

    /**
     * 设置控件监听事件
     */
    private void setListeners() {

    }
}
