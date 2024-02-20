package com.example.bitmarket.models;
public class Bid {
    private String uid; // User ID of the bidder
    private String bidValue;

    public Bid(String uid, String bidValue) {
        this.uid = uid;
        this.bidValue = bidValue;
    }

    public Bid() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBidValue() {
        return bidValue;
    }

    public void setBidValue(String bidValue) {
        this.bidValue = bidValue;
    }
    // Constructors, getters, and setters
}
