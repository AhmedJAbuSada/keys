package com.keys.chats.model;

import io.realm.RealmObject;

public class Message extends RealmObject {

    private Boolean deleted;
    private String audio;
    private String groupId;
    private String objectId;
    private String picture;
    private String senderId;
    private String senderInitials;
    private String senderName;
    private String status;
    private String text;
    private String type;
    private String video;
    private double latitude;
    private double longitude;
    private double audioDuration;
    private double createdAt;
    private double pictureHeight;
    private double pictureWidth;
    private double updatedAt;
    private double videoDuration;

    public Message() {
    }

    public Message(String groupId, String senderId, String senderName, String text, double createdAt,
                   String objectId, String type) {
        this.groupId = groupId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.text = text;
        this.createdAt = createdAt;
        this.objectId = objectId;
        this.type = type;
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.audio = "";
        this.audioDuration = 0.0;
        this.deleted = false;
        this.picture = "";
        this.pictureHeight = 0.0;
        this.pictureWidth = 0.0;
        this.senderInitials = "";
        this.status = "";
        this.updatedAt = 0.0;
        this.video = "";
        this.videoDuration = 0.0;
    }

    public Message(String audio, double audioDuration, double createdAt, String groupId,
                   String objectId, String senderId, String senderName, String type) {
        this.audio = audio;
        this.audioDuration = audioDuration;
        this.createdAt = createdAt;
        this.groupId = groupId;
        this.objectId = objectId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.type = type;
        this.text = "";
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.deleted = false;
        this.picture = "";
        this.pictureHeight = 0.0;
        this.pictureWidth = 0.0;
        this.senderInitials = "";
        this.status = "";
        this.updatedAt = 0.0;
        this.video = "";
        this.videoDuration = 0.0;
    }

    public Message(String picture, double pictureHeight, double pictureWidth, String senderId,
                   String senderName, String type, double createdAt,
                   String groupId, String objectId) {
        this.picture = picture;
        this.pictureHeight = pictureHeight;
        this.pictureWidth = pictureWidth;
        this.createdAt = createdAt;
        this.objectId = objectId;
        this.groupId = groupId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.type = type;
        this.video = "";
        this.videoDuration = 0.0;
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.audio = "";
        this.audioDuration = 0.0;
        this.deleted = false;
        this.senderInitials = "";
        this.status = "";
        this.text = "";
        this.updatedAt = 0.0;
    }

    public Message(double createdAt, String groupId, String objectId, String senderId, String senderName,
                   String type, String video, double videoDuration) {
        this.createdAt = createdAt;
        this.groupId = groupId;
        this.objectId = objectId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.type = type;
        this.video = video;
        this.videoDuration = videoDuration;
        this.picture = "";
        this.pictureHeight = 0.0;
        this.pictureWidth = 0.0;
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.audio = "";
        this.audioDuration = 0.0;
        this.deleted = false;
        this.senderInitials = "";
        this.status = "";
        this.text = "";
        this.updatedAt = 0.0;
    }

    public Message(String groupId, String senderId, String senderName, double latitude, double longitude,
                   double createdAt, String objectId, String type) {
        this.groupId = groupId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
        this.objectId = objectId;
        this.audio = "";
        this.audioDuration = 0.0;
        this.deleted = false;
        this.picture = "";
        this.pictureHeight = 0.0;
        this.pictureWidth = 0.0;
        this.senderInitials = "";
        this.status = "";
        this.text = "";
        this.type = type;
        this.updatedAt = 0.0;
        this.video = "";
        this.videoDuration = 0.0;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public double getAudioDuration() {
        return audioDuration;
    }

    public void setAudioDuration(double audioDuration) {
        this.audioDuration = audioDuration;
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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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

    public double getPictureHeight() {
        return pictureHeight;
    }

    public void setPictureHeight(double pictureHeight) {
        this.pictureHeight = pictureHeight;
    }

    public double getPictureWidth() {
        return pictureWidth;
    }

    public void setPictureWidth(double pictureWidth) {
        this.pictureWidth = pictureWidth;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderInitials() {
        return senderInitials;
    }

    public void setSenderInitials(String senderInitials) {
        this.senderInitials = senderInitials;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public double getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(double videoDuration) {
        this.videoDuration = videoDuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        return objectId.equals(message.objectId);

    }

    @Override
    public int hashCode() {
        return objectId.hashCode();
    }
}
