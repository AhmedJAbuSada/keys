package com.keys.Hraj.Model;

/**
 * Created by Fatima on 1/17/2017.
 */
public class AdsMain {
    private String name_ads, name_user, time;
    private int image;

    public AdsMain(String name_ads, String name_user, String time, int image) {
        this.name_ads = name_ads;
        this.name_user = name_user;
        this.time = time;
        this.image = image;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName_ads() {
        return name_ads;
    }

    public void setName_ads(String name_ads) {
        this.name_ads = name_ads;
    }

    public String getName_user() {
        return name_user;
    }

    public void setName_user(String name_user) {
        this.name_user = name_user;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
