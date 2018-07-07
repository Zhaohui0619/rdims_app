package com.ctkj.xj_app.util;

import com.baidu.mapapi.model.LatLng;
import com.ctkj.xj_app.bean.FacilityMapInfo;
import com.ctkj.xj_app.bean.InspectException;
import com.ctkj.xj_app.bean.InspectItem;
import com.multilevel.treelist.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * created by zhaohui on 2018/6/1 14:23
 */

public class CollectionsUtil {

    /**
     * 全局的异常信息列表
     */
    private static ArrayList<InspectException> mExceptionList = new ArrayList<>(0);

    /**
     * 添加异常数据
     * @param exceptionList
     */
    public static void addExceptions(ArrayList<InspectException> exceptionList){
        mExceptionList.addAll(exceptionList);
    }

    /**
     * 获取全局异常信息列表
     * @return
     */
    public static ArrayList<InspectException> getExceptionList(){
        return CollectionsUtil.mExceptionList;
    }

    /**
     * 清空全局异常信息列表
     */
    public static void clearExceptionList(){
        CollectionsUtil.mExceptionList = new ArrayList<>(0);
    }

    /**
     * 根据 Id 在 FacilityMapInfo列表中查找对应的bean
     * @param id
     * @param mapInfos
     * @return
     */
    public static FacilityMapInfo getMapInfo(Integer id, List<FacilityMapInfo> mapInfos){
        for (FacilityMapInfo mapInfo : mapInfos){
            if (mapInfo.getId().equals(id)){
                return mapInfo;
            }
        }
        return null;
    }

    /**
     * 根据设施 Id 在所有的巡检项中查找该设施所对应的巡检项
     * @param facilityId
     * @param items
     * @return
     */
    public static ArrayList<InspectItem> getItemsByFId(Integer facilityId, ArrayList<InspectItem> items){
        ArrayList<InspectItem> itemList = new ArrayList<>(0);
        for(InspectItem item : items){
            if (item.getFacilityId().equals(facilityId)){
                itemList.add(item);
            }
        }
        return itemList;
    }

    /**
     * 全局的巡检位置信息列表
     */
    private static ArrayList<LatLng> mLatLngList = new ArrayList<>(0);

    /**
     * 添加位置信息数据
     * @param latLng
     */
    public static void addLatLng(LatLng latLng){
        mLatLngList.add(latLng);
    }

    /**
     * 获取全局巡检位置信息列表
     * @return
     */
    public static ArrayList<LatLng> getLatLngList(){
        return CollectionsUtil.mLatLngList;
    }

    /**
     * 获取字符串类型的巡检路径信息
     * @return
     */
    public static ArrayList<String> getPathList(){
        ArrayList<String> pathList = new ArrayList<>(0);
        String str = "";
        for(LatLng latLng : getLatLngList()){
            latLng = CoordTransformUtils.bd09_to_wgs84(latLng);
            str = latLng.longitude+","+latLng.latitude;
            pathList.add(str);
        }
        return pathList;
    }

    /**
     * 清空全局巡检位置信息列表
     */
    public static void clearLatLngList(){
        CollectionsUtil.mLatLngList = new ArrayList<>(0);
    }

    /**
     * 根据设施Id在 nodeList 中修改对应的nodeName
     * @param fId
     * @param nodeList
     */
    public static void changeItemName(Integer fId, List<Node> nodeList){
        for(Node node : nodeList){
            if (node.getId().equals(fId.toString()))
                node.setName(node.getName()+" (已巡检) ");
        }
    }
}
