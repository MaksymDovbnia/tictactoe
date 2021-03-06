package com.bigtictactoeonlinegame.mainactivity;

import android.os.*;

import com.config.*;
import com.google.android.gms.ads.*;

/**
 * Created by Maksym on 12.01.14.
 */
public abstract class GeneralAdActivity extends GoogleAnalyticsActivity{

    public static final String MY_AD_UNIT_ID = "ca-app-pub-8596819956320879/9908452347";
    private AdView adView;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (Config.IS_ADD_ENABLE) {
            adView = getAdView();
            if (adView != null) {
                AdRequest adRequest = new AdRequest.Builder()
                        .build();
                adView.loadAd(adRequest);
            }
        }
    }

    public abstract AdView getAdView();


    @Override
    protected void onPause() {
        super.onPause();
        if (adView != null) {
            adView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adView != null) {
            adView.destroy();
        }
    }
}