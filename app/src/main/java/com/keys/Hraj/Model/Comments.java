package com.keys.Hraj.Model;

/**
 * Created by Fatima on 2/6/2017.
 */

public class Comments {
    public String commentId, advId, userName, message, date;

    public Comments() {
    }

    public Comments(String date, String advId, String userName, String message, String commentId) {
        this.date = date;
        this.advId = advId;
        this.userName = userName;
        this.message = message;
        this.commentId = commentId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getAdvId() {
        return advId;
    }

    public void setAdvId(String advId) {
        this.advId = advId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
