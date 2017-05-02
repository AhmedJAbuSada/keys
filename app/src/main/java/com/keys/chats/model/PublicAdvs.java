
package com.keys.chats.model;

import io.realm.RealmObject;

public class PublicAdvs extends RealmObject{

    private Boolean checked;
    private Boolean isActive;
    private Integer id;
    private String date1;
    private String date2;
    private String time1;
    private String time2;
    private String title;
    private String url;
//    private String videoUrl;
//    private int type;

    public PublicAdvs() {
    }

    public PublicAdvs(Boolean checked, Boolean isActive, Integer id, String date1, String date2,
                      String time1, String time2, String title, String url/*, String videoUrl, int type*/) {
        this.checked = checked;
        this.isActive = isActive;
        this.id = id;
        this.date1 = date1;
        this.date2 = date2;
        this.time1 = time1;
        this.time2 = time2;
        this.title = title;
        this.url = url;
//        this.videoUrl = videoUrl;
//        this.type = type;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public String getDate1() {
        return date1;
    }

    public void setDate1(String date1) {
        this.date1 = date1;
    }

    public String getDate2() {
        return date2;
    }

    public void setDate2(String date2) {
        this.date2 = date2;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getTime1() {
        return time1;
    }

    public void setTime1(String time1) {
        this.time1 = time1;
    }

    public String getTime2() {
        return time2;
    }

    public void setTime2(String time2) {
        this.time2 = time2;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
