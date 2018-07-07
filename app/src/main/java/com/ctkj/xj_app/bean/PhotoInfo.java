package com.ctkj.xj_app.bean;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * created by zhaohui on 2018/5/15 12:25
 */

public class PhotoInfo implements Serializable {

    private static final long serialVersionUID = 2019160215884755190L;

    private String name;
    private Date date;
    private Date createDate;
    private List<String> pics;
    private int postion;


    public PhotoInfo() {
        name = "自定义名称";
        date = new Date();
        createDate = new Date();
    }

    public PhotoInfo(JSONObject jsonObj) {
        name = jsonObj.optString("name");
    }

    public int getPostion() {
        return postion;
    }

    public void setPostion(int postion) {
        this.postion = postion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public List<String> getPics() {
        return pics;
    }

    public void setPics(List<String> picss) {//从图库获取或者拍照取到的图片，需要和之前数据源取并集，不能直接替换

    }

    public void resetPics(List<String> pics) {//上传体检照片时用到，需要直接替换pics
        this.pics = pics;
    }

}
