package com.keys.Hraj.Model;

/**
 * Created by Fatima on 1/29/2017.
 */

public class Departments {
    int id;
    String name;
    int isActive;

    public Departments() {
    }

    public Departments(String name, int isActive) {
        this.name = name;
        this.isActive = isActive;
    }

    public Departments(int id, String name, int isActive) {
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
