package com.ctkj.xj_app.bean;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 昭辉 on 2018/5/11.
 */

public class FacilityMapInfo implements Parcelable {

    private Integer id;
    private String name;
    private double latitude;
    private double longitude;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public FacilityMapInfo(Integer id, String name, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected FacilityMapInfo(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FacilityMapInfo> CREATOR = new Creator<FacilityMapInfo>() {
        @Override
        public FacilityMapInfo createFromParcel(Parcel in) {
            return new FacilityMapInfo(in);
        }

        @Override
        public FacilityMapInfo[] newArray(int size) {
            return new FacilityMapInfo[size];
        }
    };

    @Override
    public String toString() {
        return "FacilityMapInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
