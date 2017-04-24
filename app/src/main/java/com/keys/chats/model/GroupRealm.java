package com.keys.chats.model;

import io.realm.RealmList;
import io.realm.RealmObject;

public class GroupRealm extends RealmObject {
    private RealmList<Members> members;
    private double createdAt;
    private double updatedAt;
    private boolean deleted;
    private boolean statusId;
    private boolean checked;
    private boolean isActive;
    private String name;
    private String objectId;
    private String userId;
    private String picture;

    public GroupRealm() {
    }

    public GroupRealm(double createdAt, RealmList<Members> members, String name, String objectId,
                      double updatedAt, String userId, String picture) {
        this.checked = false;
        this.deleted = false;
        this.isActive = false;
        this.statusId = false;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.members = members;
        this.name = name;
        this.objectId = objectId;
        this.userId = userId;
        this.picture = picture;
    }

    public boolean isStatusId() {
        return statusId;
    }

    public void setStatusId(boolean statusId) {
        this.statusId = statusId;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public double getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(double createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        deleted = deleted;
    }

    public RealmList<Members> getMembers() {
        return members;
    }

    public void setMembers(RealmList<Members> members) {
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public double getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(double updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        String member = "[";
        for (int i = 0; i < members.size(); i++) {
            member += members.get(i).getMemberId() + ",";
        }
        member += "]";
        return "Group{" +
                "objectId='" + objectId + '\'' +
                ", userId='" + member + '\'' +
                '}';
    }
}
