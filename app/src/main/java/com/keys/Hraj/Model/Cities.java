package com.keys.Hraj.Model;


public class Cities {
    int id;
    String name;
    int isActive;

    public Cities() {
    }

    public Cities(String name) {
        this.name = name;
    }

    public Cities(String name, int isActive) {
        this.name = name;
        this.isActive = isActive;
    }

    public Cities(int id, String name, int isActive) {
        this.id = id;
        this.name = name;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }
}
