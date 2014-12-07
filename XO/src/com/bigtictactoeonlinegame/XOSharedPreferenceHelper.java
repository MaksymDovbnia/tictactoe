package com.bigtictactoeonlinegame;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Maksym on 06.12.2014.
 */
public class XOSharedPreferenceHelper {

    private static final String USER_NAME = "USER_NAME";
    private static final String USER_IMAGE_NAME = "USER_IMAGE_NAME";
    private static final String IS_USER_LOGIN_SOCIAL = "IS_USER_LOGIN_SOCIAL";
    private static SharedPreferences sharedPreferences;
    private static XOSharedPreferenceHelper xoSharedPreferenceHelper;


    public static void initialize(Context context) {
        xoSharedPreferenceHelper = new XOSharedPreferenceHelper();
        sharedPreferences = context.getSharedPreferences("general_share_prefs", Context.MODE_PRIVATE);
    }

    public static XOSharedPreferenceHelper getInstance() {
        return xoSharedPreferenceHelper;
    }

    public void saveUserName(String name) {
        sharedPreferences.edit().putString(USER_NAME, name).commit();
    }

    public String getUserName() {
        return sharedPreferences.getString(USER_NAME, "");
    }

    public void saveUserImageName(String name) {
        sharedPreferences.edit().putString(USER_IMAGE_NAME, name).commit();
    }

    public String getUserImageName() {
        return sharedPreferences.getString(USER_IMAGE_NAME, "");
    }

    public boolean isUserLoginSocial() {
        return sharedPreferences.getBoolean(IS_USER_LOGIN_SOCIAL, false);
    }

    public void userLoginSocial(boolean isLogin) {
        sharedPreferences.edit().putBoolean(IS_USER_LOGIN_SOCIAL, isLogin).commit();
    }


}
