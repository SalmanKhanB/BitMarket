package com.example.bitmarket.utils;

import com.google.firebase.auth.FirebaseAuth;

public class AppConst {
    public static String uid = "";

    public static String getUid() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            return uid;
        }
        uid = "";
        return "";
    }
}
