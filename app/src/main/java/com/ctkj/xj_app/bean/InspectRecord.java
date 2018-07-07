package com.ctkj.xj_app.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.litepal.crud.LitePalSupport;

import java.util.Date;

/**
 * created by zhaohui on 2018/5/10 9:18
 */

public class InspectRecord extends LitePalSupport implements Parcelable {

    private Integer taskId;
    private String name;
    private Date buildTime;
    private Date finishTime;
    private String status;
    private String note;

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBuildTime(Date buildTime) {
        this.buildTime = buildTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public String getName() {
        return name;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getBuildTime() {
        return buildTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getFinishTime() {
        return finishTime;
    }

    public String getStatus() {
        return status;
    }

    public String getNote() {
        return note;
    }

    protected InspectRecord(Parcel in) {
        this.taskId = in.readInt();
        this.name = in.readString();
        this.buildTime = new Date(in.readLong());
        this.finishTime = new Date(in.readLong());
        this.status = in.readString();
        this.note = in.readString();
    }

    public static final Creator<InspectRecord> CREATOR = new Creator<InspectRecord>() {
        @Override
        public InspectRecord createFromParcel(Parcel in) {
            return new InspectRecord(in);
        }

        @Override
        public InspectRecord[] newArray(int size) {
            return new InspectRecord[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.taskId);
        parcel.writeString(this.name);
        parcel.writeLong(this.buildTime.getTime());
        parcel.writeLong(this.finishTime.getTime());
        parcel.writeString(this.status);
        parcel.writeString(this.note);
    }


}
