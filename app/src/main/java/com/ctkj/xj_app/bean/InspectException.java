package com.ctkj.xj_app.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.Date;

/**
 * Created by 昭辉 on 2018/5/23.
 */

public class InspectException extends LitePalSupport implements Parcelable {

    private Integer facilityId;
    private Integer itemId;
    private String itemName;
    private String inspectDescription;
    private String exceptionPoint;
    private String inspectMultimediaList;

    public Integer getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Integer facilityId) {
        this.facilityId = facilityId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getInspectDescription() {
        return inspectDescription;
    }

    public void setInspectDescription(String inspectDescription) {
        this.inspectDescription = inspectDescription;
    }

    public String getExceptionPoint() {
        return exceptionPoint;
    }

    public void setExceptionPoint(String exceptionPoint) {
        this.exceptionPoint = exceptionPoint;
    }

    public String getInspectMultimediaList() {
        return inspectMultimediaList;
    }

    public void setInspectMultimediaList(String inspectMultimediaList) {
        this.inspectMultimediaList = inspectMultimediaList;
    }

    public InspectException(Integer facilityId, Integer itemId, String itemName, String inspectDescription, String exceptionPoint, String inspectMultimediaList) {
        this.facilityId = facilityId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.inspectDescription = inspectDescription;
        this.exceptionPoint = exceptionPoint;
        this.inspectMultimediaList = inspectMultimediaList;
    }

    public InspectException() {

    }

    protected InspectException(Parcel in) {
        this.facilityId = in.readInt();
        this.itemId = in.readInt();
        this.itemName = in.readString();
        this.inspectDescription = in.readString();
        this.exceptionPoint = in.readString();
        this.inspectMultimediaList = in.readString();
    }

    public static final Creator<InspectException> CREATOR = new Creator<InspectException>() {
        @Override
        public InspectException createFromParcel(Parcel in) {
            return new InspectException(in);
        }

        @Override
        public InspectException[] newArray(int size) {
            return new InspectException[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.facilityId);
        parcel.writeInt(this.itemId);
        parcel.writeString(this.itemName);
        parcel.writeString(this.inspectDescription);
        parcel.writeString(this.exceptionPoint);
        parcel.writeString(this.inspectMultimediaList);
    }

    @Override
    public String toString() {
        return "InspectException{" +
                "facilityId=" + facilityId +
                ", itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", inspectDescription='" + inspectDescription + '\'' +
                ", exceptionPoint='" + exceptionPoint + '\'' +
                ", inspectMultimediaList='" + inspectMultimediaList + '\'' +
                '}';
    }
}
