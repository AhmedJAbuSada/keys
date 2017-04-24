package com.keys.chats.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotifictionObj {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("recipients")
    @Expose
    private Integer recipients;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getRecipients() {
        return recipients;
    }

    public void setRecipients(Integer recipients) {
        this.recipients = recipients;
    }

}

