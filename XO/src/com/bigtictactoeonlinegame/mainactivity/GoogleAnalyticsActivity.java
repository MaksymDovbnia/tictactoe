package com.bigtictactoeonlinegame.mainactivity;

import android.os.*;
import android.support.v4.app.*;

import com.google.analytics.tracking.android.*;

/**
 * Created by Maksym on 10.02.14.
 */
public class GoogleAnalyticsActivity extends FragmentActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }
}