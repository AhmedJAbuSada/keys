package com.keys.Hraj.Model;

/**
 * Created by Fatima on 1/17/2017.
 */
public class Market {
    public String id, name, address, mobile, description, url, image_market, date, userId;
    public int isActive;

    public Market(String id, String name, String address, String mobile, String description, String url, String image_market, String date, int isActive, String userId) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.mobile = mobile;
        this.description = description;
        this.url = url;
        this.image_market = image_market;
        this.date = date;
        this.isActive = isActive;
        this.userId = userId;
    }

    public Market(String name, String address, String image) {
        this.name = name;
        this.image_market = image;
        this.address = address;
    }

    public Market() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage_market() {
        return image_market;
    }

    public void setImage_market(String image_market) {
        this.image_market = image_market;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }
}
