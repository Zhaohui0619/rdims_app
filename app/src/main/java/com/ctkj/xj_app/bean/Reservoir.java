package com.ctkj.xj_app.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * created by zhaohui on 2018/5/10 9:19
 */

public class Reservoir implements Parcelable {

    private Integer id;
    private String name;
    private Double longitude;
    private Double latitude;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Reservoir() {

    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Reservoir(Integer id, String name, Double longitude, Double latitude) {
        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    protected Reservoir(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
    }

    public static final Creator<Reservoir> CREATOR = new Creator<Reservoir>() {
        @Override
        public Reservoir createFromParcel(Parcel in) {
            return new Reservoir(in);
        }

        @Override
        public Reservoir[] newArray(int size) {
            return new Reservoir[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeString(this.name);
        parcel.writeDouble(this.longitude);
        parcel.writeDouble(this.latitude);
    }
}
