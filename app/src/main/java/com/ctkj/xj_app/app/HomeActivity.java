package com.ctkj.xj_app.app;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.ctkj.xj_app.R;
import com.ctkj.xj_app.bean.FacilityMapInfo;
import com.ctkj.xj_app.bean.FacilityNode;
import com.ctkj.xj_app.bean.InspectFacility;
import com.ctkj.xj_app.bean.InspectItem;
import com.ctkj.xj_app.bean.InspectPlan;
import com.ctkj.xj_app.bean.InspectRecord;
import com.ctkj.xj_app.bean.InspectTask;
import com.ctkj.xj_app.bean.ReservoirInfo;
import com.ctkj.xj_app.bean.UserInfo;
import com.ctkj.xj_app.http.VolleyJSONObjectListener;
import com.ctkj.xj_app.http.VolleyRequestUtil;
import com.ctkj.xj_app.util.CommonUtils;
import com.ctkj.xj_app.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    @BindView(R.id.test_widget_11)
    TextView userName_tv;

    private Integer reservoirId;
    private Integer inspectUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        reservoirId = getIntent().getIntExtra("reservoirId", 0);
        inspectUserId = getIntent().getIntExtra("inspectUserId", 0);
        //根据巡检人员ID 进行查询，得到巡检人员相关信息
        UserInfo userInfo = LitePal.where("userId = ?", inspectUserId.toString()).find(UserInfo.class).get(0);
        userName_tv.setText(userInfo.getUserName());
    }

    @OnClick(R.id.test_widget_10)
    public void onUserClick(View view) {
        Intent intent = new Intent(this, UserInfoActivity.class);
        intent.putExtra("inspectUserId", inspectUserId);
        startActivity(intent);
    }

    @OnClick(R.id.test_widget_4)
    public void onReservoirClick(View view) {
        Properties properties = CommonUtils.getProperties(getApplicationContext(), "appConfig");
        String serverUrl = properties.getProperty("serverUrl");
//        serverUrl = serverUrl + "reservoir/getById?id=" + reservoirId;
        serverUrl = serverUrl + "reservoir/forApp/getByReservoirId?reservoirId=" + reservoirId;
        String requestTag = "reservoirInfo request";

        VolleyRequestUtil.jsonObjectRequestGet(serverUrl, requestTag,
                new VolleyJSONObjectListener(VolleyJSONObjectListener.mListener, VolleyJSONObjectListener.mErrorListener) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {
                        LogUtil.w(TAG, "onMySuccess: " + jsonObject.toString());
                        try {
                            Gson gson = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create();
                            ReservoirInfo reservoirInfo = gson.fromJson(jsonObject.getString("data"), ReservoirInfo.class);
                            //将水库信息添加到数据库
                            reservoirInfo.saveOrUpdate("reservoirId = ?", reservoirInfo.getReservoirId().toString());
                            Intent intent = new Intent(HomeActivity.this, ReservoirInfoActivity.class);
                            //将水库ID传到下个界面最为查询条件
                            intent.putExtra("reservoirId", reservoirInfo.getReservoirId());
//                            intent.putExtra("reservoirInfo",reservoirInfo);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        LogUtil.w(TAG, "onMyError: " + error);
                    }
                }, true);
    }

    @OnClick(R.id.test_widget_24)
    public void onFacilityClick(View view) {
        Properties properties = CommonUtils.getProperties(getApplicationContext(), "appConfig");
        String serverUrl = properties.getProperty("serverUrl");
        serverUrl = serverUrl + "inspect/forApp/getFacilityList?reservoirId=" + reservoirId;
        String requestTag = "inspectFacilities request";

        VolleyRequestUtil.jsonObjectRequestGet(serverUrl, requestTag,
                new VolleyJSONObjectListener(VolleyJSONObjectListener.mListener, VolleyJSONObjectListener.mErrorListener) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {
                        LogUtil.w(TAG, "onMySuccess: " + jsonObject.toString());
                        try {

                            Gson gson = new Gson();
                            ArrayList<InspectFacility> facilityList =
                                    gson.fromJson(jsonObject.getString("data"), new TypeToken<ArrayList<InspectFacility>>() {
                                    }.getType());
                            //讲水库设施保存到数据库
                            saveFacilityNode(facilityList);
                            Properties properties = CommonUtils.getProperties(getApplicationContext(), "appConfig");
                            String serverUrl = properties.getProperty("serverUrl");
                            serverUrl = serverUrl + "inspect/forApp/getItemList?reservoirId=" + reservoirId;
                            String requestTag = "items request";

                            VolleyRequestUtil.jsonObjectRequestGet(serverUrl, requestTag,
                                    new VolleyJSONObjectListener(VolleyJSONObjectListener.mListener, VolleyJSONObjectListener.mErrorListener) {
                                        @Override
                                        public void onMySuccess(JSONObject jsonObject) {
                                            try {
                                                Intent intent = new Intent(HomeActivity.this, InspectionFacilitiesActivity.class);
                                                Gson gson = new Gson();
                                                ArrayList<InspectItem> itemList =
                                                        gson.fromJson(jsonObject.getString("data"), new TypeToken<ArrayList<InspectItem>>() {
                                                        }.getType());
                                                LogUtil.w(TAG,"onMySuccess"+itemList.toString());
                                                //将巡检设施添加到数据库
                                                for (InspectItem item : itemList)
                                                    item.saveOrUpdate("itemId = ?", item.getItemId().toString());
                                                intent.putExtra("title", "水库检测设施");
                                                intent.putExtra("reservoirId", reservoirId);
                                                intent.putExtra("inspectUserId", inspectUserId);
//                                                intent.putParcelableArrayListExtra("facilityList",facilityList);
//                                                intent.putParcelableArrayListExtra("itemList",itemList);
                                                startActivity(intent);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onMyError(VolleyError error) {

                                        }
                                    }, true);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        LogUtil.w(TAG, "onMyError: " + error);
                    }
                }, true);
    }

    private void saveFacilityNode(ArrayList<InspectFacility> facilities){
        for (InspectFacility facility : facilities) {
            String[] cPoint = facility.getCenterPorint().split(",");
            if(LitePal.where("childId = ?",facility.getId().toString()).find(FacilityNode.class).size() > 0){
                ContentValues values = new ContentValues();
                values.put("name",facility.getName());
                LitePal.update(FacilityNode.class,values,facility.getId());
            }else {
                FacilityNode node =
                        new FacilityNode(facility.getReservoirId(), facility.getpId(), facility.getId(), facility.getName(), Double.parseDouble(cPoint[0]), Double.parseDouble(cPoint[1]), facility.getFlevel(), facility.getFtype(), facility.getCode(), 0);
                node.save();
            }
            saveFacilityNode(facility.getChildFacilities());
        }
        LogUtil.w(TAG,LitePal.where("reservoirId = ?",reservoirId.toString()).find(FacilityNode.class).toString());
    }

    @OnClick(R.id.test_widget_20)
    public void onRecordClick(View view) {
        Properties properties = CommonUtils.getProperties(getApplicationContext(), "appConfig");
        String serverUrl = properties.getProperty("serverUrl");
        serverUrl = serverUrl + "inspect/forApp/historyTaskList?reservoirId=" + reservoirId;
        String requestTag = "inspectRecords request";

        VolleyRequestUtil.jsonObjectRequestGet(serverUrl, requestTag,
                new VolleyJSONObjectListener(VolleyJSONObjectListener.mListener, VolleyJSONObjectListener.mErrorListener) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {
                        LogUtil.w(TAG, "onMySuccess: " + jsonObject.toString());
                        try {
                            Intent intent = new Intent(HomeActivity.this, InspectionRecordsActivity.class);
                            Gson gson = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create();
                            ArrayList<InspectRecord> recordList =
                                    gson.fromJson(jsonObject.getString("data"), new TypeToken<ArrayList<InspectRecord>>() {
                                    }.getType());
                            //将巡检记录添加到数据库
                            for (InspectRecord record : recordList)
                                record.saveOrUpdate("taskId = ?", record.getTaskId().toString());

                            intent.putParcelableArrayListExtra("recordList", recordList);
                            intent.putExtra("title", "历史任务");
                            LogUtil.w(TAG, "onMySuccess: " + recordList.toString());
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        LogUtil.w(TAG, "onMyError: " + error);
                    }
                }, true);
    }

    @OnClick(R.id.test_widget_21)
    public void onTaskClick(View view) {
        Properties properties = CommonUtils.getProperties(getApplicationContext(), "appConfig");
        String serverUrl = properties.getProperty("serverUrl");
        serverUrl = serverUrl + "inspect/forApp/unfinishedTaskList?reservoirId=" + reservoirId;
        String requestTag = "inspectTasks request";

        VolleyRequestUtil.jsonObjectRequestGet(serverUrl, requestTag,
                new VolleyJSONObjectListener(VolleyJSONObjectListener.mListener, VolleyJSONObjectListener.mErrorListener) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {
                        LogUtil.w(TAG, "onMySuccess: " + jsonObject.toString());
                        try {
                            Intent intent = new Intent(HomeActivity.this, InspectionTasksActivity.class);
                            Gson gson = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create();
                            ArrayList<InspectTask> taskList =
                                    gson.fromJson(jsonObject.getString("data"), new TypeToken<ArrayList<InspectTask>>() {
                                    }.getType());
                            //将巡检任务添加到数据库
                            for (InspectTask task : taskList)
                                task.saveOrUpdate("taskId = ?", task.getTaskId().toString());

                            intent.putParcelableArrayListExtra("taskList", taskList);
                            intent.putExtra("title", "当前任务");
                            LogUtil.w(TAG, "onMySuccess: " + taskList.toString());
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        LogUtil.w(TAG, "onMyError: " + error);
                    }
                }, true);

    }

    @OnClick(R.id.test_widget_22)
    public void onPlanClick(View view) {
        Properties properties = CommonUtils.getProperties(getApplicationContext(), "appConfig");
        String serverUrl = properties.getProperty("serverUrl");
        serverUrl = serverUrl + "inspect/forApp/planList?reservoirId=" + reservoirId;
        String requestTag = "inspectPlans request";

        VolleyRequestUtil.jsonObjectRequestGet(serverUrl, requestTag,
                new VolleyJSONObjectListener(VolleyJSONObjectListener.mListener, VolleyJSONObjectListener.mErrorListener) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {
                        LogUtil.w(TAG, "onMySuccess: " + jsonObject.toString());
                        try {
                            Intent intent = new Intent(HomeActivity.this, InspectionPlansActivity.class);
                            Gson gson = new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .create();
                            ArrayList<InspectPlan> planList =
                                    gson.fromJson(jsonObject.getString("data"), new TypeToken<ArrayList<InspectPlan>>() {
                                    }.getType());
                            //将巡检计划添加到数据库
                            for (InspectPlan plan : planList)
                                plan.saveOrUpdate("planId = ?", plan.getPlanId().toString());

                            intent.putParcelableArrayListExtra("planList", planList);
                            intent.putExtra("title", "巡检计划");
                            LogUtil.w(TAG, "onMySuccess: " + planList.toString());
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        LogUtil.w(TAG, "onMyError: " + error);
                    }
                }, true);

    }

    @OnClick(R.id.test_widget_23)
    public void onFacilityMapClick(View view) {
        Properties properties = CommonUtils.getProperties(getApplicationContext(), "appConfig");
        String serverUrl = properties.getProperty("serverUrl");
        serverUrl = serverUrl + "inspect/forApp/getFacilityMapList?reservoirId=" + reservoirId;
        String requestTag = "facilityMap request";
        VolleyRequestUtil.jsonObjectRequestGet(serverUrl, requestTag,
                new VolleyJSONObjectListener(VolleyJSONObjectListener.mListener, VolleyJSONObjectListener.mErrorListener) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {
                        LogUtil.w(TAG, "onMySuccess: " + jsonObject.toString());
                        try {
                            Intent intent = new Intent(HomeActivity.this, InspectionMapActivity.class);
                            Gson gson = new Gson();
                            ArrayList<FacilityMapInfo> facilityMapList =
                                    gson.fromJson(jsonObject.getString("data"), new TypeToken<ArrayList<FacilityMapInfo>>() {
                                    }.getType());
                            intent.putParcelableArrayListExtra("facilityMapList", facilityMapList);
                            intent.putExtra("title", "监测设施地图");
                            LogUtil.w(TAG, "onMySuccess: " + facilityMapList.toString());
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        LogUtil.w(TAG, "onMyError: " + error);
                    }
                }, true);

    }

}
