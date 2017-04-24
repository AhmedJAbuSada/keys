package com.keys.chats.model;

import java.util.ArrayList;
import java.util.List;

public class Recent {
    private List<String> members = new ArrayList<>();
    private Boolean archived, deleted;
    private String description, groupId, initials, lastMessage, objectId, picture, type, userId;
    private Integer counter;
    private double lastMessageDate, createdAt, updatedAt;

    public Recent(List<String> members, String groupId, String lastMessage, String objectId,
                  String picture, String type, String userId, double time) {
        this.members = members;
        this.groupId = groupId;
        this.lastMessage = lastMessage;
        this.objectId = objectId;
        this.picture = picture;
        this.type = type;
        this.userId = userId;
        this.lastMessageDate = time;
        this.createdAt = time;
        this.updatedAt = time;
        this.archived = false;
        this.deleted = false;
        this.description = " ";
        this.initials = " ";
        this.counter = 0;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public double getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(double createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public double getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(double lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

}
