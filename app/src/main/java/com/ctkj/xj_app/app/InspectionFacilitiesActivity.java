package com.ctkj.xj_app.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.ctkj.xj_app.R;
import com.ctkj.xj_app.adapter.SimpleTreeRecyclerAdapter;
import com.ctkj.xj_app.bean.FacilityMapInfo;
import com.ctkj.xj_app.bean.FacilityNode;
import com.ctkj.xj_app.bean.InspectException;
import com.ctkj.xj_app.bean.InspectExceptionVoA;
import com.ctkj.xj_app.bean.InspectFacility;
import com.ctkj.xj_app.bean.InspectItem;
import com.ctkj.xj_app.util.CollectionsUtil;
import com.ctkj.xj_app.util.CommonUtils;
import com.ctkj.xj_app.util.CoordTransformUtils;
import com.ctkj.xj_app.util.LogUtil;
import com.hotdog.hdlibrary.core.HDToast;
import com.multilevel.treelist.Node;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * created by zhaohui on 2018/5/10 8:58
 */

public class InspectionFacilitiesActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "InspectionFacilitiesActivity";

    private static final int INSPECT_ITEM = 1;

    private LatLng sLatLng;
    private Integer reservoirId;
    private Integer taskId;
    private Integer inspectUserId;
    private String inspectStartTime;
    private String inspectEndTime;

    @BindView(R.id.mv_facilities)
    MapView mMapView;

    private PopupWindow mPopupWindow;
    private View contentView;

    private BaiduMap mMap;
    private LocationClient mLocationClient;
    private InfoWindow mInfoWindow;
    private Boolean isFirstLocate = true;

    private ArrayList<FacilityNode> fNodeList = new ArrayList<>(0);
    private ArrayList<InspectException> exceptionList = new ArrayList<>(0);
    private List<Node> nodeList = new ArrayList<>(0);
    private SimpleTreeRecyclerAdapter mAdapter;

    private List<Marker> markerList = new ArrayList<>(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_inspection_facilities);
        ButterKnife.bind(this);

        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        startLocate();

        reservoirId = getIntent().getIntExtra("reservoirId", 0);
//        taskId = getIntent().getIntExtra("taskId",0);
        inspectUserId = getIntent().getIntExtra("inspectUserId", 0);
        fNodeList = (ArrayList<FacilityNode>) LitePal.where("reservoirId = ?",reservoirId.toString()).find(FacilityNode.class);
        LogUtil.w(TAG,fNodeList.toString());

        mMap = mMapView.getMap();
        mMap.setMyLocationEnabled(true);
        facilityMarker(this);

        showPopWindow();
    }


    private void showPopWindow() {
        contentView = LayoutInflater.from(this).inflate(R.layout.inspect_facility_popwindow, null);
        RecyclerView facilityList_rv = contentView.findViewById(R.id.rv_inspectionFacility);
        Button startInspect_btn = contentView.findViewById(R.id.btn_startInspect);
        Button stopInspect_btn = contentView.findViewById(R.id.btn_stopInspect);
        View dismissPopWin_view = contentView.findViewById(R.id.view_dismiss);

        startInspect_btn.setOnClickListener(this);
        stopInspect_btn.setOnClickListener(this);
        dismissPopWin_view.setOnClickListener(this);

        getNodeList(fNodeList);
        facilityList_rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        facilityList_rv.setLayoutManager(lm);
        mAdapter = new SimpleTreeRecyclerAdapter(facilityList_rv, this, nodeList, 1, R.mipmap.tree_ex, R.mipmap.tree_ec);
        facilityList_rv.setAdapter(mAdapter);

        mAdapter.setClickListener(new SimpleTreeRecyclerAdapter.onTreeRecyclerItemClickListener() {
            @Override
            public void onItemClick(Node node) {
                if (node != null) {
                    if (node.isLeaf()) {
                        String name = node.getName();
                        Integer id = Integer.parseInt(node.getId().toString());
                        FacilityNode fNode = LitePal.where("childId = ?",id.toString()).find(FacilityNode.class).get(0);
                        if (fNode.getStatus() == 1 || fNode.getStatus() == -1) {
                            HDToast.show(fNode.getName()+"已经巡检过了",InspectionFacilitiesActivity.this);
                        } else {
                            mMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(new LatLng(fNode.getLatitude(), fNode.getLongitude())));
                        }
                        Intent intent = new Intent(InspectionFacilitiesActivity.this, InspectionExecActivity.class);
                        intent.putExtra("title", name);
                        intent.putExtra("leafFId", id);
                        startActivityForResult(intent, INSPECT_ITEM);
                    }
                }
            }
        });

        //popupWindow宽度和高度设置
        mPopupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setFocusable(false);
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setAnimationStyle(R.style.facilityPopWindow_anim_style);
    }

    /**
     * 解析facilityList
     *
     * @param facilityNodes
     */
    private void getNodeList(List<FacilityNode> facilityNodes) {
        if (facilityNodes != null) {
            for (FacilityNode fNode : facilityNodes) {
                nodeList.add(new Node(fNode.getChildId() + "", fNode.getParentId() + "", fNode.getName()));
            }
            LogUtil.w(TAG,nodeList.toString());
        }
    }

    /**
     * 地图上显示所有巡检设施的地理位置
     */
    private void facilityMarker(Context context) {
        mMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(18.0f));
        LatLng location = null;
        OverlayOptions options = null;
        Marker marker = null;

        List<LatLng> latLngList = new ArrayList<>(0);
        for (FacilityNode fNode : fNodeList) {

            location = new LatLng(fNode.getLatitude(), fNode.getLongitude());
            location = CoordTransformUtils.wgs84_to_bd09(location);
            latLngList.add(location);
            BitmapDescriptor bitmap = null;
            if (fNode.getStatus() == 0) {
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_facility_marker_sb);
            }else if(fNode.getStatus() == -1){
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_facility_marker_sr);
            }else if(fNode.getStatus() == 1){
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_facility_marker_sg);
            }
            options = new MarkerOptions()
                    .title(fNode.getName())
                    .position(location)
                    .icon(bitmap);

            marker = (Marker) mMap.addOverlay(options);
            Bundle bundle = new Bundle();
            bundle.putParcelable("facility", fNode);
            marker.setExtraInfo(bundle);
            markerList.add(marker);
            mMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    final FacilityNode node = (FacilityNode) marker.getExtraInfo().get("facility");

//                        TextView reservoirName = new TextView(getApplicationContext());
//                        reservoirName.setBackgroundResource(R.drawable.ic_reservoir_marker);
//                        reservoirName.setPadding(30,20,30,50);
//                        reservoirName.setText(mapInfo.getName());

                    Button reservoirName = new Button(getApplicationContext());
                    reservoirName.setText(node.getName());
                    BitmapDescriptor nameDes = BitmapDescriptorFactory.fromView(reservoirName);
                    LatLng ll = marker.getPosition();
                    Point point = mMap.getProjection().toScreenLocation(ll);
                    point.y -= 47;
                    LatLng llInfo = mMap.getProjection().fromScreenLocation(point);

                    mInfoWindow = new InfoWindow(nameDes, llInfo, 0, new InfoWindow.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick() {

                        }
                    });
                    mMap.showInfoWindow(mInfoWindow);

                    return true;
                }
            });

        }

        mMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(location));

        mMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });

    }


    /**
     * 更换地图上facility对应的 marker 的图标
     * 三种状态：已巡检（未发生异常）、已巡检（发生异常）、未巡检
     * @param cFacilityId
     */
    private void changeItemMarker(Integer cFacilityId){
        FacilityNode facilityNode = LitePal.where("childId = ?",cFacilityId.toString()).find(FacilityNode.class).get(0);
        BitmapDescriptor bitmap1 = BitmapDescriptorFactory.fromResource(R.drawable.ic_facility_marker_sg);
        BitmapDescriptor bitmap2 = BitmapDescriptorFactory.fromResource(R.drawable.ic_facility_marker_sr);
        Iterator<Marker> iterator = markerList.iterator();
        while (iterator.hasNext()){
            Marker marker = iterator.next();
            FacilityNode fNode = (FacilityNode) marker.getExtraInfo().get("facility");
            if (fNode.getChildId().equals(cFacilityId)){
                if (facilityNode.getStatus() == 1) {
                    marker.setIcon(bitmap1);
                }else if(facilityNode.getStatus() == -1){
                    marker.setIcon(bitmap2);
                }
            }
        }
    }

    private void startLocate() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setScanSpan(2000);
        option.setOpenGps(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    private void navigateTo(BDLocation location){
        if (isFirstLocate) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
            isFirstLocate = false;
        }

        MyLocationData data = new MyLocationData.Builder()
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
        mMap.setMyLocationData(data);

        /**
         * 定位图层的配置（定位模式:罗盘态，是否允许方向信息:是，自定义定位图标，自定义定位精度圈的填充颜色和边框颜色）
         * NORMAL （普通态）: 更新定位数据时不对地图做任何动作
         * FOLLOWING （跟随态）: 保持定位图标在地图中心
         * COMPASS （罗盘态）: 显示定位方向圈，保持定位图标在地图中心
         */
        MyLocationConfiguration.LocationMode  mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;
        BitmapDescriptor mMarker = BitmapDescriptorFactory.fromResource(R.drawable.self_location);
        mMap.setMyLocationConfiguration(new MyLocationConfiguration(mLocationMode,true,mMarker,0xAAFFFF88,0xAAFFFF88));

    }

    @OnClick(R.id.btn_facility)
    void openPopWindow() {
        mMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(18.0f));
        mAdapter.notifyDataSetChanged();
        mPopupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
    }

    @OnClick(R.id.btn_returnSelf)
    void returnReservoir() {
        mMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(18.0f));
        mMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(sLatLng));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_startInspect:
                AlertDialog.Builder startDialog = new AlertDialog.Builder((this))
                        .setMessage("巡检开始并进行实时位置信息的采集")
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //确定开始的话将开始位置采集，并获取当前时间为巡检开始时间
                                inspectStartTime = CommonUtils.getFormatDateStr();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //暂不做处理
                            }
                        });
                startDialog.show();

                break;
            case R.id.btn_stopInspect:
                AlertDialog.Builder stopDialog = new AlertDialog.Builder((this))
                        .setMessage("巡检结束并停止实时位置信息的采集")
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //位置采集停止
                                mLocationClient.stop();
                                //巡检结束时间
                                inspectEndTime = CommonUtils.getFormatDateStr();
//                                InspectExceptionVoA exceptionVoA =
//                                        new InspectExceptionVoA(reservoirId, 1886, inspectUserId, CollectionsUtil.getExceptionList(), CommonUtils.getInspectPath("pathData.txt", InspectionFacilitiesActivity.this), inspectStartTime, inspectEndTime);
                               InspectExceptionVoA exceptionVoA =
                                       new InspectExceptionVoA(reservoirId,1908,inspectUserId,exceptionList,CollectionsUtil.getPathList(),inspectStartTime,inspectEndTime);
                                CommonUtils.createJsonFile(InspectionFacilitiesActivity.this, exceptionVoA);
                                //清空LatLngList
                                CollectionsUtil.clearLatLngList();
                                //是否需要清空ExceptionList？？？
//                                CollectionsUtil.clearExceptionList();
//                                LitePal.deleteAll(InspectException.class);
                                Intent intent = new Intent(InspectionFacilitiesActivity.this,HomeActivity.class);
                                intent.putExtra("reservoirId",reservoirId);
                                intent.putExtra("inspectUserId",inspectUserId);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //暂不做处理
                            }
                        });
                stopDialog.show();
                break;
            case R.id.view_dismiss:
                mPopupWindow.dismiss();
                mMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(sLatLng));
            default:
                break;
        }
    }

    /**
     * 定位监听器，新接口异步获取位置信息
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation || bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
                navigateTo(bdLocation);
                sLatLng = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
//                CollectionsUtil.addLatLng(CoordTransformUtils.bd09_to_wgs84(sLatLng));
                CollectionsUtil.addLatLng(sLatLng);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case INSPECT_ITEM:
                if (resultCode == RESULT_OK) {
                    mPopupWindow.dismiss();
                    Integer cFacilityId = data.getIntExtra("dataReturn", 0);
//                    ArrayList<InspectException> exceptionList = data.getParcelableArrayListExtra("exceptionList");
                    //使用数据库查询
                    exceptionList =
                            (ArrayList<InspectException>) LitePal.where("facilityId = ?",cFacilityId.toString())
                                    .find(InspectException.class);
//                    CollectionsUtil.addExceptions(exceptionList);
                    //回调使改变node内容（已巡检）
//                    CollectionsUtil.changeItemName(cFacilityId,nodeList);
                    FacilityNode fNode = new FacilityNode();
                    if(exceptionList.size() > 0) {
                        //将其状态改为 已巡检，有异常
                        fNode.setStatus(-1);
                    }else if(exceptionList.isEmpty()){
                        //将其状态改为 已巡检，无异常
                        fNode.setStatus(1);
                    }
                    fNode.updateAll("childId = ?",cFacilityId.toString());
                    mAdapter.notifyDataSetChanged();
                    //该巡检设施巡检结果提交之后，修改marker的图标和title
                    changeItemMarker(cFacilityId);
                    //在地图上画出该该巡检设施的巡检路径
                    OverlayOptions lineOption = new PolylineOptions()
                            .color(0xAAFF0000)
                            .width(5)
                            .points(CollectionsUtil.getLatLngList());
                    mMap.addOverlay(lineOption);
                    mMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(sLatLng));
                    mMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(18.0f));
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        //不需要时销毁MapView和定位图层
        mMapView.onDestroy();
        mMap.setMyLocationEnabled(false);
    }
}
