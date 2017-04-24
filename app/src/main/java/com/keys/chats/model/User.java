package com.keys.chats.model;

import java.io.Serializable;

import io.realm.RealmObject;

public class User extends RealmObject implements Serializable{
    private String country, deviceToken, displayStatus, email, fullName,
            latitude, longitude, mobileNo, objectId, os, picture, userQRCode, statusId;
    private double updatedAt, createdAt;

    public User() {
    }

    public User(String country, String displayStatus, String deviceToken, String email, String fullName,
                String latitude, String longitude, String mobileNo, String objectId,String os, String picture,
                String userQRCode, double updatedAt, double createdAt, String statusId) {
        this.country = country;
        this.deviceToken = deviceToken;
        this.displayStatus = displayStatus;
        this.email = email;
        this.fullName = fullName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mobileNo = mobileNo;
        this.objectId = objectId;
        this.os = os;
        this.picture = picture;
        this.userQRCode = userQRCode;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.statusId = statusId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(double createdAt) {
        this.createdAt = createdAt;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(String displayStatus) {
        this.displayStatus = displayStatus;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public double getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(double updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUserQRCode() {
        return userQRCode;
    }

    public void setUserQRCode(String userQRCode) {
        this.userQRCode = userQRCode;
    }
//    public User(String country, String displayStatus, String deviceToken, String email, String fullName,
//                String latitude, String longitude, String mobileNo, String objectId, String picture,
//                String userQRCode, String statusId, double updatedAt, double createdAt) {
//        this.statusId = statusId;
//        this.country = country;
//        this.deviceToken = deviceToken;
//        this.displayStatus = displayStatus;
//        this.email = email;
//        this.fullName = fullName;
//        this.latitude = latitude;
//        this.longitude = longitude;
//        this.mobileNo = mobileNo;
//        this.objectId = objectId;
//        this.os = "Android";
//        this.picture = picture;
//        this.userQRCode = userQRCode;
//        this.updatedAt = updatedAt;
//        this.createdAt = createdAt;
//    }

}
