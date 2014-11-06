package com.bigtictactoeonlinegame.mainactivity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.example.games.basegameutils.BaseGameActivity;

/**
 * Created by Maksym on 10.02.14.
 */
public abstract class GoogleAnalyticsWithPlayServiceActivity extends BaseGameActivity {
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