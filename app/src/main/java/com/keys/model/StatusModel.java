package com.keys.model;

/**
 * Created by Fatima on 12/21/2016.
 */
public class StatusModel {

    private String title;
    private int image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    int id;

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
    public StatusModel(int id, String title) {
        this.title = title;
        this.id = id;
    }
    public StatusModel(int id, String title, int image) {
        this.title = title;
        this.image = image;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
