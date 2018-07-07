package com.ctkj.xj_app.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;

/**
 * created by zhaohui on 2018/5/10 9:18
 */

public class InspectFacility extends LitePalSupport implements Parcelable {

    private Integer id;
    private int pId;
    private Integer reservoirId;
    private String name;
    private Integer flevel;
    private String ftype;
    private String centerPorint;
    private ArrayList<InspectFacility> childFacilities = new ArrayList<>(0);
    private String code;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setpId(int pId) {
        this.pId = pId;
    }

    public void setReservoirId(Integer reservoirId) {
        this.reservoirId = reservoirId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFlevel(Integer flevel) {
        this.flevel = flevel;
    }

    public void setFtype(String ftype) {
        this.ftype = ftype;
    }

    public void setCenterPorint(String centerPorint) {
        this.centerPorint = centerPorint;
    }

    public void setChildFacilities(ArrayList<InspectFacility> childFacilities) {
        this.childFacilities = childFacilities;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public Integer getId() {
        return id;
    }

    public Integer getpId() {
        return pId;
    }

    public Integer getReservoirId() {
        return reservoirId;
    }

    public String getName() {
        return name;
    }

    public Integer getFlevel() {
        return flevel;
    }

    public String getFtype() {
        return ftype;
    }

    public String getCenterPorint() {
        return centerPorint;
    }

    public ArrayList<InspectFacility> getChildFacilities() {
        return childFacilities;
    }

    public String getCode() {
        return code;
    }

    protected InspectFacility(Parcel in) {
        this.id = in.readInt();
        this.pId = in.readInt();
        this.reservoirId = in.readInt();
        this.name = in.readString();
        this.flevel = in.readInt();
        this.ftype = in.readString();
        this.centerPorint = in.readString();
        this.childFacilities = new ArrayList<>();
        in.readList(this.childFacilities, InspectFacility.class.getClassLoader());
        this.code = in.readString();
    }

    public static final Creator<InspectFacility> CREATOR = new Creator<InspectFacility>() {
        @Override
        public InspectFacility createFromParcel(Parcel in) {
            return new InspectFacility(in);
        }

        @Override
        public InspectFacility[] newArray(int size) {
            return new InspectFacility[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeInt(this.pId);
        parcel.writeInt(this.reservoirId);
        parcel.writeString(this.name);
        parcel.writeInt(this.flevel);
        parcel.writeString(this.ftype);
        parcel.writeString(this.centerPorint);
        parcel.writeList(this.childFacilities);
        parcel.writeString(this.code);
    }

    @Override
    public String toString() {
        return "InspectFacility{" +
                "id=" + id +
                ", pId=" + pId +
                ", reservoirId=" + reservoirId +
                ", name='" + name + '\'' +
                ", flevel=" + flevel +
                ", ftype='" + ftype + '\'' +
                ", centerPorint='" + centerPorint + '\'' +
                ", childFacilities=" + childFacilities +
                ", code='" + code + '\'' +
                '}';
    }
}
