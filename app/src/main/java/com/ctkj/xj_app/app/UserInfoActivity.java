package com.ctkj.xj_app.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ctkj.xj_app.R;
import com.ctkj.xj_app.bean.UserInfo;
import com.ctkj.xj_app.util.LogUtil;
import com.ctkj.xj_app.widget.TitleInfoView;

import org.litepal.LitePal;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserInfoActivity extends AppCompatActivity {

    private static final String TAG = "UserInfoActivity";

    @BindView(R.id.tiv_name)
    TitleInfoView tiv_userName;

    private Integer inspectUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        ButterKnife.bind(this);

        inspectUserId = getIntent().getIntExtra("inspectUserId",0);
        //根据巡检人员ID 进行查询，得到巡检人员相关信息
        LogUtil.w(TAG,LitePal.where("userId = ?",inspectUserId.toString()).find(UserInfo.class).toString());
        UserInfo userInfo  =  LitePal.where("userId = ?",inspectUserId.toString()).find(UserInfo.class).get(0);
        tiv_userName.setInfo(userInfo.getUserName());
    }
}
