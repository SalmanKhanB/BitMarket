package com.example.bitmarket.utils;

import java.util.HashMap;
import java.util.Map;

public class Users {
    String email="";
    String phone="";
    String name="";
    String status="";
    String profileImage="";

    public Users() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfileImage() {
        return profileImage != null ? profileImage : "";
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("phone", phone);
        userMap.put("name", name);
        userMap.put("status", status);
        userMap.put("profileImage", profileImage);
        return userMap;
    }
}
