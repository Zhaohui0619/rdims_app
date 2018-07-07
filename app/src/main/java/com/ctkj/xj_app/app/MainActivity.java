package com.ctkj.xj_app.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

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
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.ctkj.xj_app.R;
import com.ctkj.xj_app.bean.Reservoir;
import com.ctkj.xj_app.util.CoordTransformUtils;
import com.ctkj.xj_app.util.LogUtil;
import com.hotdog.hdlibrary.core.HDToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.mv_reservoirs)
    MapView reservoirs_mv;

    private BaiduMap mMap;
    private LocationClient mLocationClient;
    private InfoWindow mInfoWindow;

    private ArrayList<Reservoir> reservoirList;
    private Integer inspectUserId;
    private Boolean isFirstLocate = true;

    private LatLng selfLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setTitle("水库地图概览");
        mMap = reservoirs_mv.getMap();
        mMap.setMyLocationEnabled(true);

        inspectUserId = getIntent().getIntExtra("inspectUserId",0);
        reservoirList = getIntent().getParcelableArrayListExtra("reservoirList");
        LogUtil.w(TAG,reservoirList.toString());
        reservoirsMarker(this);
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
            requestLocation();
        }

    }

    /**
     * 在地图上显示水库图标，并设置点击事件
     * @param context
     */
    private void reservoirsMarker(final Context context) {
        LatLng location = null;
        OverlayOptions options = null;
        Marker marker = null;
        for (Reservoir reservoir : reservoirList) {

            location = new LatLng(reservoir.getLatitude(), reservoir.getLongitude());
            location = CoordTransformUtils.wgs84_to_bd09(location);
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_reservoir_marker);
            options = new MarkerOptions()
                    .title(reservoir.getName())
                    .position(location)
                    .icon(bitmap);

            marker = (Marker) mMap.addOverlay(options);
            Bundle bundle = new Bundle();
            bundle.putParcelable("reservoir", reservoir);
            marker.setExtraInfo(bundle);

            mMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    final Reservoir r = (Reservoir) marker.getExtraInfo().get("reservoir");

//                        TextView reservoirName = new TextView(getApplicationContext());
//                        reservoirName.setBackgroundResource(R.drawable.ic_reservoir_marker);
//                        reservoirName.setPadding(30,20,30,50);
//                        reservoirName.setText(r.getName());

                    Button reservoirName = new Button(getApplicationContext());
                    reservoirName.setText(r.getName());
                    BitmapDescriptor nameDes = BitmapDescriptorFactory.fromView(reservoirName);
                    LatLng ll = marker.getPosition();
                    Point point = mMap.getProjection().toScreenLocation(ll);
                    point.y -= 47;
                    LatLng llInfo = mMap.getProjection().fromScreenLocation(point);

                    mInfoWindow = new InfoWindow(nameDes, llInfo, 0, new InfoWindow.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick() {
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            intent.putExtra("reservoirId", r.getId());
                            intent.putExtra("inspectUserId", inspectUserId);
                            context.startActivity(intent);
                        }
                    });
                    mMap.showInfoWindow(mInfoWindow);

                    return true;
                }
            });

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
//        mMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(location));

    }

    private void requestLocation(){
        initLocation();
        mLocationClient.start();
    }

    /**
     * 定位基本设置
     */
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setScanSpan(5000);
        option.setCoorType("bd09ll");
        option.setOpenGps(true);
        mLocationClient.setLocOption(option);
    }


    private void navigateTo(BDLocation location){
        if (isFirstLocate) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(latLng);
            mMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16.5f);
            mMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        MyLocationData data = new MyLocationData.Builder()
                .accuracy(location.getRadius())
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

    private class MyLocationListener extends BDAbstractLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if(bdLocation.getLocType() == BDLocation.TypeGpsLocation || bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                selfLocation = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
                navigateTo(bdLocation);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            HDToast.show("必须同意所有权限才能使用本程序",this);
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    HDToast.show("发生未知错误",this);
                    finish();
                }
                break;
            default:
        }
    }

    @OnClick(R.id.iv_returnSelf)
    void returnMySelf(){
        mMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(selfLocation));
    }

    @Override
    protected void onResume() {
        super.onResume();
        reservoirs_mv.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        reservoirs_mv.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        reservoirs_mv.onDestroy();
        mMap.setMyLocationEnabled(false);
    }
}
