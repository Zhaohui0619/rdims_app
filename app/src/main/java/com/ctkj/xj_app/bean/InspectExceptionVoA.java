package com.ctkj.xj_app.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.mapapi.model.LatLng;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.ArrayList;
import java.util.Date;

/**
 * created by zhaohui on 2018/6/2 13:37
 */

public class InspectExceptionVoA implements Parcelable {

    private Integer reservoirId;
    private Integer taskId;
    private Integer inspectUserId;
    private ArrayList<InspectException> exceptionList;
    private ArrayList<String> inspectPathList;
    private  String inspectStartTime;//巡检开始时间
    private  String inspectEndTime;//巡检结束时间

    public Integer getReservoirId() {
        return reservoirId;
    }

    public void setReservoirId(Integer reservoirId) {
        this.reservoirId = reservoirId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getInspectUserId() {
        return inspectUserId;
    }

    public void setInspectUserId(Integer inspectUserId) {
        this.inspectUserId = inspectUserId;
    }

    public ArrayList<InspectException> getExceptionList() {
        return exceptionList;
    }

    public void setExceptionList(ArrayList<InspectException> exceptionList) {
        this.exceptionList = exceptionList;
    }

    public ArrayList<String> getInspectPathList() {
        return inspectPathList;
    }

    public void setInspectPathList(ArrayList<String> inspectPathList) {
        this.inspectPathList = inspectPathList;
    }

    public  String getInspectStartTime() {
        return inspectStartTime;
    }

    public  void setInspectStartTime(String inspectStartTime) {
        this.inspectStartTime = inspectStartTime;
    }

    public  String getInspectEndTime() {
        return inspectEndTime;
    }

    public  void setInspectEndTime(String inspectEndTime) {
        this.inspectEndTime = inspectEndTime;
    }

    public InspectExceptionVoA(Integer reservoirId, Integer taskId, Integer inspectUserId, ArrayList<InspectException> exceptionList, ArrayList<String> inspectPathList, String inspectStartTime, String inspectEndTime) {
        this.reservoirId = reservoirId;
        this.taskId = taskId;
        this.inspectUserId = inspectUserId;
        this.exceptionList = exceptionList;
        this.inspectPathList = inspectPathList;
        this.inspectStartTime = inspectStartTime;
        this.inspectEndTime = inspectEndTime;
    }

    protected InspectExceptionVoA(Parcel in) {
        this.reservoirId = in.readInt();
        this.taskId = in.readInt();
        this.inspectUserId = in.readInt();
        this.exceptionList = new ArrayList<>();
        in.readList(exceptionList,InspectExceptionVoA.class.getClassLoader());
        this.inspectPathList = new ArrayList<>();
        in.readList(inspectPathList,InspectExceptionVoA.class.getClassLoader());
        this.inspectStartTime = in.readString();
        this.inspectEndTime = in.readString();
//        this.inspectStartTime = new Date(in.readLong());
//        this.inspectEndTime = new Date(in.readLong());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.reservoirId);
        dest.writeInt(this.taskId);
        dest.writeInt(this.inspectUserId);
        dest.writeList(this.exceptionList);
        dest.writeList(this.inspectPathList);
        dest.writeString(this.inspectStartTime);
        dest.writeString(this.inspectEndTime);
//        dest.writeLong(this.inspectStartTime.getTime());
//        dest.writeLong(this.inspectEndTime.getTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<InspectExceptionVoA> CREATOR = new Creator<InspectExceptionVoA>() {
        @Override
        public InspectExceptionVoA createFromParcel(Parcel in) {
            return new InspectExceptionVoA(in);
        }

        @Override
        public InspectExceptionVoA[] newArray(int size) {
            return new InspectExceptionVoA[size];
        }
    };
}
