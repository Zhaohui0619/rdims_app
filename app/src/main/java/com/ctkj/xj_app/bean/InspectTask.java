package com.ctkj.xj_app.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.litepal.crud.LitePalSupport;

import java.util.Date;

/**
 * created by zhaohui on 2018/5/10 9:18
 */


public class InspectTask extends LitePalSupport implements Parcelable {

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

    protected InspectTask(Parcel in) {
        this.taskId = in.readInt();
        this.name = in.readString();
        this.buildTime = new Date(in.readLong());
        this.finishTime = new Date(in.readLong());
        this.status = in.readString();
        this.note = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.taskId);
        dest.writeString(this.name);
        dest.writeLong(this.buildTime.getTime());
        dest.writeLong(this.finishTime.getTime());
        dest.writeString(this.status);
        dest.writeString(this.note);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<InspectTask> CREATOR = new Creator<InspectTask>() {
        @Override
        public InspectTask createFromParcel(Parcel in) {
            return new InspectTask(in);
        }

        @Override
        public InspectTask[] newArray(int size) {
            return new InspectTask[size];
        }
    };

    @Override
    public String toString() {
        return "InspectTask{" +
                "taskId=" + taskId +
                ", name='" + name + '\'' +
                ", buildTime=" + buildTime +
                ", finishTime=" + finishTime +
                ", status='" + status + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
