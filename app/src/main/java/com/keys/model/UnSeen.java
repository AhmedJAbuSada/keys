package com.keys.model;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class UnSeen {
    String userId, adsId;
    int count;

    public UnSeen() {
    }

    public UnSeen(String userId, String adsId, int count) {
        this.userId = userId;
        this.adsId = adsId;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getAdsId() {
        return adsId;
    }

    public void setAdsId(String adsId) {
        this.adsId = adsId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("advId", adsId);
        result.put("userId", userId);
        result.put("count", count);
        return result;
    }
}
