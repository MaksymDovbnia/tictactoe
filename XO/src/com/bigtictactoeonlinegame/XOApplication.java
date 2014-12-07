package com.bigtictactoeonlinegame;

import android.app.Application;

/**
 * Created by Maksym on 06.12.2014.
 */
public class XOApplication extends Application {

    private static XOApplication application;

    public static XOApplication getInstance() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        XOSharedPreferenceHelper.initialize(this);
    }

}
