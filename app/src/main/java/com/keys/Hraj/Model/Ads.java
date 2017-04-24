package com.keys.Hraj.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class Ads implements Serializable {
    private String AdvId, title, department, city, shop, description, Latitude, Longitude, date, userName, shopId;
    private boolean isActive;
    private ArrayList<String> imageUrls;

    public Ads() {
    }

    public Ads(String advId, String title, String department, String city, String shopId,
               String shop, String description, ArrayList<String> imageUrls, String latitude,
               String longitude, String date, String userName, boolean isActive) {
        AdvId = advId;
        this.title = title;
        this.department = department;
        this.city = city;
        this.shopId = shopId;
        this.shop = shop;
        this.description = description;
        this.imageUrls = imageUrls;
        Latitude = latitude;
        Longitude = longitude;
        this.date = date;
        this.userName = userName;
        this.isActive = isActive;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getAdvId() {
        return AdvId;
    }

    public void setAdvId(String advId) {
        this.AdvId = advId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public ArrayList<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}

