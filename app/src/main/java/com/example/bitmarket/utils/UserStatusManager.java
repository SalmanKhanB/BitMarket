package com.example.bitmarket.utils;
import android.content.Context;
import android.content.SharedPreferences;

public class UserStatusManager {
    private static final String PREF_NAME = "UserStatusPref";
    private static final String KEY_USER_STATUS = "userStatus";

    public static void setUserStatus(Context context, String status) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_STATUS, status);
        editor.apply();
    }

    public static String getUserStatus(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_STATUS, "");
    }
    public static  boolean isBuyer(Context context){
        String userStatus = getUserStatus(context);
        if ("Buyer".equals(userStatus)) {
            return true;
        } else if ("Seller".equals(userStatus)) {
            return false;
        }
        return  false;
    }
    public static void removeUserStatus(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER_STATUS);
        editor.apply();
    }

}
