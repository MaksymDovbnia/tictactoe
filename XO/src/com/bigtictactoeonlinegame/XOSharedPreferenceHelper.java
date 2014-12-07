package com.bigtictactoeonlinegame;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Maksym on 06.12.2014.
 */
public class XOSharedPreferenceHelper {

    private static SharedPreferences sharedPreferences;
    private static XOSharedPreferenceHelper xoSharedPreferenceHelper;

    public static void initialize(Context context) {
        xoSharedPreferenceHelper = new XOSharedPreferenceHelper();
        sharedPreferences = context.getSharedPreferences("geeral", Context.MODE_PRIVATE);
    }

    public static XOSharedPreferenceHelper getInstance() {
        return xoSharedPreferenceHelper;
    }
}
