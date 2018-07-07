package com.ctkj.xj_app.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.ctkj.xj_app.R;
import com.ctkj.xj_app.bean.FacilityMapInfo;
import com.ctkj.xj_app.util.CollectionsUtil;
import com.ctkj.xj_app.util.CoordTransformUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InspectionMapActivity extends AppCompatActivity {

    @BindView(R.id.mv_inspection)
    MapView inspection_mv;

    private BaiduMap mMap;
    private ArrayList<FacilityMapInfo> facilityMapList = new ArrayList<>(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_map);
        ButterKnife.bind(this);

        facilityMapList = getIntent().getParcelableArrayListExtra("facilityMapList");
        setTitle(getIntent().getStringExtra("title"));
        mMap = inspection_mv.getMap();
        facilityMarker(this);
    }


    /**
     * 在地图上标记巡检设施
     */
    private void facilityMarker(final Context context) {
        mMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(19.0f));
        LatLng location = null;
        OverlayOptions options = null;
        Marker marker = null;

        List<LatLng> latLngList = new ArrayList<>(0);
        for (FacilityMapInfo facilityMap : facilityMapList) {

            location = new LatLng(facilityMap.getLatitude(), facilityMap.getLongitude());
            CoordTransformUtils.wgs84_to_bd09(location);
            latLngList.add(location);
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_reservoir_marker);
            options = new MarkerOptions()
                    .title(facilityMap.getName())
                    .position(location)
                    .icon(bitmap);

            marker = (Marker) mMap.addOverlay(options);
            Bundle bundle = new Bundle();
            bundle.putParcelable("facilityMap", facilityMap);
            marker.setExtraInfo(bundle);

            mMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    final FacilityMapInfo mapInfo = (FacilityMapInfo) marker.getExtraInfo().get("facilityMap");

                    InfoWindow mInfoWindow;
//                        TextView reservoirName = new TextView(getApplicationContext());
//                        reservoirName.setBackgroundResource(R.drawable.ic_reservoir_marker);
//                        reservoirName.setPadding(30,20,30,50);
//                        reservoirName.setText(mapInfo.getName());

                    Button reservoirName = new Button(getApplicationContext());
                    reservoirName.setText(mapInfo.getName());
                    BitmapDescriptor nameDes = BitmapDescriptorFactory.fromView(reservoirName);
                    LatLng ll = marker.getPosition();
                    Point point = mMap.getProjection().toScreenLocation(ll);
                    point.y -= 47;
                    LatLng llInfo = mMap.getProjection().fromScreenLocation(point);

                    mInfoWindow = new InfoWindow(nameDes, llInfo, 0, new InfoWindow.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick() {
                            Intent intent = new Intent(InspectionMapActivity.this, InspectionExecActivity.class);
                            intent.putExtra("title", mapInfo.getName());
                            context.startActivity(intent);
                        }
                    });
                    mMap.showInfoWindow(mInfoWindow);

                    return true;
                }
            });

        }

        OverlayOptions lineOption = new PolylineOptions()
                .color(0xAAFF0000)
                .width(10)
                .points(latLngList);
        mMap.addOverlay(lineOption);
        mMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(location));

    }

    @Override
    protected void onResume() {
        super.onResume();
        inspection_mv.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        inspection_mv.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        inspection_mv.onDestroy();
    }
}
