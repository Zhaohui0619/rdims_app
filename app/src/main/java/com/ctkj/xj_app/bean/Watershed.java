package com.ctkj.xj_app.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * created by zhaohui on 2018/5/10 9:19
 */

public class Watershed implements Parcelable {

    private int id;
    private String name;

    private Watershed() {

    }

    protected Watershed(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
    }

    public static final Creator<Watershed> CREATOR = new Creator<Watershed>() {
        @Override
        public Watershed createFromParcel(Parcel in) {
            return new Watershed(in);
        }

        @Override
        public Watershed[] newArray(int size) {
            return new Watershed[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
