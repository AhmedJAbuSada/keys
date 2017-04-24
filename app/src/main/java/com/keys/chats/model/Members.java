package com.keys.chats.model;

import io.realm.RealmObject;

public class Members extends RealmObject {
    public String memberId;

    public Members() {
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
}
