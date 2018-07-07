package com.ctkj.xj_app.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.LitePalSupport;

/**
 * Created by mocking on 2018/6/22.
 */

public class FacilityNode extends LitePalSupport implements Parcelable {

    private Integer reservoirId;
    private Integer parentId;
    private Integer childId;
    private String name;
    private Double latitude;
    private Double longitude;
    private Integer flevel;
    private String ftype;
    private String code;

    /** 巡检设施状态
     * -1 已巡检，有异常
     * 0 未巡检
     * 1 已巡检，无异常
     */
    private Integer status;

    public Integer getReservoirId() {
        return reservoirId;
    }

    public void setReservoirId(Integer reservoirId) {
        this.reservoirId = reservoirId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getChildId() {
        return childId;
    }

    public void setChildId(Integer childId) {
        this.childId = childId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getFlevel() {
        return flevel;
    }

    public void setFlevel(Integer flevel) {
        this.flevel = flevel;
    }

    public String getFtype() {
        return ftype;
    }

    public void setFtype(String ftype) {
        this.ftype = ftype;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    protected FacilityNode(Parcel in) {
        this.reservoirId = in.readInt();
        this.parentId = in.readInt();
        this.childId = in.readInt();
        this.name = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.flevel = in.readInt();
        this.ftype = in.readString();
        this.code = in.readString();
        this.status = in.readInt();
    }

    public FacilityNode() {
    }

    public FacilityNode(Integer reservoirId, Integer parentId, Integer childId, String name, Double latitude, Double longitude, Integer flevel, String ftype, String code, Integer status) {
        this.reservoirId = reservoirId;
        this.parentId = parentId;
        this.childId = childId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.flevel = flevel;
        this.ftype = ftype;
        this.code = code;
        this.status = status;
    }

    public static final Creator<FacilityNode> CREATOR = new Creator<FacilityNode>() {
        @Override
        public FacilityNode createFromParcel(Parcel in) {
            return new FacilityNode(in);
        }

        @Override
        public FacilityNode[] newArray(int size) {
            return new FacilityNode[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.reservoirId);
        parcel.writeInt(this.parentId);
        parcel.writeInt(this.childId);
        parcel.writeString(this.name);
        parcel.writeDouble(this.latitude);
        parcel.writeDouble(this.longitude);
        parcel.writeInt(this.flevel);
        parcel.writeString(this.ftype);
        parcel.writeString(this.code);
        parcel.writeInt(this.status);
    }

    @Override
    public String toString() {
        return "FacilityNode{" +
                "reservoirId=" + reservoirId +
                ", parentId=" + parentId +
                ", childId=" + childId +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", flevel=" + flevel +
                ", ftype='" + ftype + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
