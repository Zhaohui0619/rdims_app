package com.ctkj.xj_app.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.litepal.crud.LitePalSupport;

import java.util.Date;

/**
 * created by zhaohui on 2018/5/10 9:19
 */

public class ReservoirInfo extends LitePalSupport implements Parcelable {
    private Integer reservoirId;
    private String name;
    private Integer provinceId;
    private Integer cityId;
    private Integer countyId;
    private Double longitude;
    private Double latitude;
    private String code;
    private Date beginBuildTime;
    private Date completedBuildTime;
    private String degree;
    private String description;
    private Double deadWaterLevel;
    private Double floodStorageCapacity;
    private String administrativeRegionsCode;
    private Double preFloodSeasonLimitWaterLevel;
    private Double mainFloodSeasonLimitWaterLevel;
    private Double afterFloodSeasonLimitWaterLevel;
    private Integer sort;
    private Integer delFlag;
    private String provinceName;
    private String cityName;
    private String areaName;
    private String url;

    public Integer getReservoirId() {
        return reservoirId;
    }

    public void setReservoirId(Integer reservoirId) {
        this.reservoirId = reservoirId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getCountyId() {
        return countyId;
    }

    public void setCountyId(Integer countyId) {
        this.countyId = countyId;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+08")
    public Date getBeginBuildTime() {
        return beginBuildTime;
    }

    public void setBeginBuildTime(Date beginBuildTime) {
        this.beginBuildTime = beginBuildTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+08")
    public Date getCompletedBuildTime() {
        return completedBuildTime;
    }

    public void setCompletedBuildTime(Date completedBuildTime) {
        this.completedBuildTime = completedBuildTime;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getDeadWaterLevel() {
        return deadWaterLevel;
    }

    public void setDeadWaterLevel(Double deadWaterLevel) {
        this.deadWaterLevel = deadWaterLevel;
    }

    public Double getFloodStorageCapacity() {
        return floodStorageCapacity;
    }

    public void setFloodStorageCapacity(Double floodStorageCapacity) {
        this.floodStorageCapacity = floodStorageCapacity;
    }

    public String getAdministrativeRegionsCode() {
        return administrativeRegionsCode;
    }

    public void setAdministrativeRegionsCode(String administrativeRegionsCode) {
        this.administrativeRegionsCode = administrativeRegionsCode;
    }

    public Double getPreFloodSeasonLimitWaterLevel() {
        return preFloodSeasonLimitWaterLevel;
    }

    public void setPreFloodSeasonLimitWaterLevel(Double preFloodSeasonLimitWaterLevel) {
        this.preFloodSeasonLimitWaterLevel = preFloodSeasonLimitWaterLevel;
    }

    public Double getMainFloodSeasonLimitWaterLevel() {
        return mainFloodSeasonLimitWaterLevel;
    }

    public void setMainFloodSeasonLimitWaterLevel(Double mainFloodSeasonLimitWaterLevel) {
        this.mainFloodSeasonLimitWaterLevel = mainFloodSeasonLimitWaterLevel;
    }

    public Double getAfterFloodSeasonLimitWaterLevel() {
        return afterFloodSeasonLimitWaterLevel;
    }

    public void setAfterFloodSeasonLimitWaterLevel(Double afterFloodSeasonLimitWaterLevel) {
        this.afterFloodSeasonLimitWaterLevel = afterFloodSeasonLimitWaterLevel;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static Creator<ReservoirInfo> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.reservoirId);
        dest.writeString(this.name);
        dest.writeInt(this.provinceId);
        dest.writeInt(this.cityId);
        dest.writeInt(this.countyId);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
        dest.writeLong(this.beginBuildTime.getTime());
        dest.writeLong(this.completedBuildTime.getTime());
        dest.writeString(this.degree);
        dest.writeString(this.description);
        dest.writeDouble(this.deadWaterLevel);
        dest.writeDouble(this.floodStorageCapacity);
        dest.writeString(this.administrativeRegionsCode);
        dest.writeDouble(this.preFloodSeasonLimitWaterLevel);
        dest.writeDouble(this.mainFloodSeasonLimitWaterLevel);
        dest.writeDouble(this.afterFloodSeasonLimitWaterLevel);
        dest.writeInt(this.sort);
        dest.writeInt(this.delFlag);
        dest.writeString(this.provinceName);
        dest.writeString(this.cityName);
        dest.writeString(this.areaName);
        dest.writeString(this.url);
    }

    public ReservoirInfo() {

    }

    protected ReservoirInfo(Parcel in) {
        this.reservoirId = in.readInt();
        this.name = in.readString();
        this.provinceId = in.readInt();
        this.cityId = in.readInt();
        this.countyId = in.readInt();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.beginBuildTime = new Date(in.readLong());
        this.completedBuildTime = new Date(in.readLong());
        this.degree = in.readString();
        this.description = in.readString();
        this.deadWaterLevel = in.readDouble();
        this.floodStorageCapacity = in.readDouble();
        this.administrativeRegionsCode = in.readString();
        this.preFloodSeasonLimitWaterLevel = in.readDouble();
        this.mainFloodSeasonLimitWaterLevel = in.readDouble();
        this.afterFloodSeasonLimitWaterLevel = in.readDouble();
        this.sort = in.readInt();
        this.delFlag = in.readInt();
        this.provinceName = in.readString();
        this.cityName = in.readString();
        this.areaName = in.readString();
        this.url = in.readString();
    }

    public static final Creator<ReservoirInfo> CREATOR = new Creator<ReservoirInfo>() {
        @Override
        public ReservoirInfo createFromParcel(Parcel source) {
            return new ReservoirInfo(source);
        }

        @Override
        public ReservoirInfo[] newArray(int size) {
            return new ReservoirInfo[size];
        }
    };
}
