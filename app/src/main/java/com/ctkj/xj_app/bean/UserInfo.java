package com.ctkj.xj_app.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.LitePalSupport;


/**
 * created by zhaohui on 2018/5/10 9:19
 */

public class UserInfo extends LitePalSupport implements Parcelable {

    private Integer userId;
    private String userName;
    private String password;
    private String phone;
    private String gender;
    private String address;

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getGender() {
        return gender;
    }

    public String getAddress() {
        return address;
    }

    protected UserInfo(Parcel in) {
        this.userId = in.readInt();
        this.userName = in.readString();
        this.password = in.readString();
        this.phone = in.readString();
        this.gender = in.readString();
        this.address = in.readString();
    }

    public UserInfo() {

    }

    public UserInfo(Integer userId, String userName, String password, String phone, String gender, String address) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.phone = phone;
        this.gender = gender;
        this.address = address;
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.userId);
        parcel.writeString(this.userName);
        parcel.writeString(this.getPassword());
        parcel.writeString(this.phone);
        parcel.writeString(this.gender);
        parcel.writeString(this.address);
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", gender='" + gender + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
