package com.ctkj.xj_app.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.LitePalSupport;

/**
 * Created by 昭辉 on 2018/5/22.
 */

public class InspectItem extends LitePalSupport implements Parcelable {

    private Integer itemId;
    private Integer reservoirId;
    private Integer facilityId;
    private String name;
    private String note;
    private String code;

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getReservoirId() {
        return reservoirId;
    }

    public void setReservoirId(Integer reservoirId) {
        this.reservoirId = reservoirId;
    }

    public Integer getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Integer facilityId) {
        this.facilityId = facilityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    protected InspectItem(Parcel in) {
        this.itemId = in.readInt();
        this.reservoirId = in.readInt();
        this.facilityId = in.readInt();
        this.name = in.readString();
        this.note = in.readString();
        this.code = in.readString();
    }


    public static final Creator<InspectItem> CREATOR = new Creator<InspectItem>() {
        @Override
        public InspectItem createFromParcel(Parcel in) {
            return new InspectItem(in);
        }

        @Override
        public InspectItem[] newArray(int size) {
            return new InspectItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.itemId);
        parcel.writeInt(this.reservoirId);
        parcel.writeInt(this.facilityId);
        parcel.writeString(this.name);
        parcel.writeString(this.note);
        parcel.writeString(this.code);
    }

    @Override
    public String toString() {
        return "InspectItem{" +
                "itemId=" + itemId +
                ", reservoirId=" + reservoirId +
                ", facilityId=" + facilityId +
                ", name='" + name + '\'' +
                ", note='" + note + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
