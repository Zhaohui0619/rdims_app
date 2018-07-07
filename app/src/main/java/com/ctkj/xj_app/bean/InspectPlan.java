package com.ctkj.xj_app.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.litepal.crud.LitePalSupport;

import java.util.Date;

/**
 * created by zhaohui on 2018/5/10 9:18
 */

public class InspectPlan extends LitePalSupport implements Parcelable {

    private Integer planId;
    private String name;
    private Date beginDate;
    private Date endDate;
    private String type;
    private String status;
    private String note;

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getPlanId() {
        return planId;
    }

    public String getName() {
        return name;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getBeginDate() {
        return beginDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getEndDate() {
        return endDate;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getNote() {
        return note;
    }

    protected InspectPlan(Parcel in) {
        this.planId = in.readInt();
        this.name = in.readString();
        this.beginDate = new Date(in.readLong());
        this.endDate = new Date(in.readLong());
        this.type = in.readString();
        this.status = in.readString();
        this.note = in.readString();

    }

    public static final Creator<InspectPlan> CREATOR = new Creator<InspectPlan>() {
        @Override
        public InspectPlan createFromParcel(Parcel in) {
            return new InspectPlan(in);
        }

        @Override
        public InspectPlan[] newArray(int size) {
            return new InspectPlan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.planId);
        parcel.writeString(this.name);
        parcel.writeLong(this.beginDate.getTime());
        parcel.writeLong(this.endDate.getTime());
        parcel.writeString(this.type);
        parcel.writeString(this.status);
        parcel.writeString(this.note);
    }

    @Override
    public String toString() {
        return "InspectPlan{" +
                "planId=" + planId +
                ", name='" + name + '\'' +
                ", beginDate=" + beginDate +
                ", endDate=" + endDate +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
