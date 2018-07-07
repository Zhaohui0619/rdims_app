package com.ctkj.xj_app.util;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by 昭辉 on 2018/5/16.
 */

public class CoordTransformUtils {
    private static Double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
    //π
    private static Double pi = 3.1415926535897932384626;
    //长半轴
    private static Double a = 6378245.0;
    //偏心率平方
    private static Double ee = 0.00669342162296594323;


    /**
     * 火星坐标系(GCJ-02)转百度坐标系(BD-09)
     * 谷歌、高德——>百度
     *
     * @param latLng 火星经纬度坐标
     * @return 转换后的坐标
     */
    public static LatLng gcj02_to_bd09(LatLng latLng) {

        Double z = Math.sqrt(latLng.longitude * latLng.longitude + latLng.latitude * latLng.latitude) + 0.00002 * Math.sin(latLng.latitude * x_pi);
        Double theta = Math.atan2(latLng.latitude, latLng.longitude) + 0.000003 * Math.cos(latLng.longitude * x_pi);
        Double bd_lng = z * Math.cos(theta) + 0.0065;
        Double bd_lat = z * Math.sin(theta) + 0.006;
        return new LatLng(bd_lat, bd_lng);
    }

    /**
     * 百度坐标系(BD-09)转火星坐标系(GCJ-02)
     * 百度——>谷歌、高德
     *
     * @param latLng 百度经纬度坐标
     * @return 转换后的坐标
     */
    public static LatLng bd09_to_gcj02(LatLng latLng) {
        Double x = latLng.longitude - 0.0065;
        Double y = latLng.latitude - 0.006;
        Double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        Double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        Double gg_lng = z * Math.cos(theta);
        Double gg_lat = z * Math.sin(theta);
        return new LatLng(gg_lat, gg_lng);
    }

    /**
     * WGS84 转 GCJ02(火星坐标系)
     *
     * @param latLng WGS84坐标系的经纬度坐标
     * @return 转换后的坐标
     */
    public static LatLng wgs84_to_gcj02(LatLng latLng) {
        if (out_of_china(latLng)) {
            return latLng;
        } else {
            Double dLat = transformLat(latLng.longitude - 105.0, latLng.latitude - 35.0);
            Double dLng = transformLng(latLng.longitude - 105.0, latLng.latitude - 35.0);
            Double radLat = latLng.latitude / 180.0 * pi;
            Double magic = Math.sin(radLat);
            magic = 1 - ee * magic * magic;
            Double sqrtMagic = Math.sqrt(magic);
            dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
            dLng = (dLng * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
            Double mgLat = latLng.latitude + dLat;
            Double mgLng = latLng.longitude + dLng;
            return new LatLng(mgLat, mgLng);
        }
    }

    /**
     * GCJ02(火星坐标系) 转 GPS84
     *
     * @param latLng 火星坐标系的经纬度坐标
     * @return 转换后的坐标
     */
    public static LatLng gcj02_to_wgs84(LatLng latLng) {
        if (out_of_china(latLng)) {
            return latLng;
        } else {
            Double dLat = transformLat(latLng.longitude - 105.0, latLng.latitude - 35.0);
            Double dLng = transformLng(latLng.longitude - 105.0, latLng.latitude - 35.0);
            Double radLat = latLng.latitude / 180.0 * pi;
            Double magic = Math.sin(radLat);
            magic = 1 - ee * magic * magic;
            Double sqrtMagic = Math.sqrt(magic);
            dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
            dLng = (dLng * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
            Double mgLat = latLng.latitude + dLat;
            Double mgLng = latLng.longitude + dLng;
            return new LatLng(mgLat, mgLng);
        }
    }

    /**
     * BD-09坐标系转WGS84坐标系
     *
     * @param latLng BD-09 经纬度坐标
     * @return 转换后的坐标
     */
    public static LatLng bd09_to_wgs84(LatLng latLng) {
        LatLng tempLatLng = bd09_to_gcj02(latLng);
        return gcj02_to_wgs84(tempLatLng);
    }

    /**
     * WGS84坐标系转BD-09坐标系
     *
     * @param latLng WGS84 经纬度坐标
     * @return 转换后的坐标
     */
    public static LatLng wgs84_to_bd09(LatLng latLng) {

        LatLng tempLatLng = wgs84_to_gcj02(latLng);
        return gcj02_to_bd09(tempLatLng);
    }


    /**
     * 判断是否在国外，是的话不做偏移
     *
     * @param latLng 经纬度坐标
     * @return 是否在国外
     */
    private static boolean out_of_china(LatLng latLng) {
        if (latLng.longitude > 73.66 && latLng.longitude < 135.05 && latLng.latitude > 3.86 && latLng.latitude < 53.55) {
            return false;
        } else
            return true;
    }

    private static Double transformLat(Double lng, Double lat) {
        Double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));

        ret += (20.0 * Math.sin(6.0 * lng * pi) + 20.0 * Math.sin(2.0 * lng * pi)) * 2.0 / 3.0;

        ret += (20.0 * Math.sin(lat * pi) + 40.0 * Math.sin(lat / 3.0 * pi)) * 2.0 / 3.0;

        ret += (160.0 * Math.sin(lat / 12.0 * pi) + 320 * Math.sin(lat * pi / 30.0)) * 2.0 / 3.0;

        return ret;
    }


    private static Double transformLng(Double lng, Double lat) {
        Double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));

        ret += (20.0 * Math.sin(6.0 * lng * pi) + 20.0 * Math.sin(2.0 * lng * pi)) * 2.0 / 3.0;

        ret += (20.0 * Math.sin(lng * pi) + 40.0 * Math.sin(lng / 3.0 * pi)) * 2.0 / 3.0;

        ret += (150.0 * Math.sin(lng / 12.0 * pi) + 300.0 * Math.sin(lng / 30.0 * pi)) * 2.0 / 3.0;

        return ret;
    }
}
