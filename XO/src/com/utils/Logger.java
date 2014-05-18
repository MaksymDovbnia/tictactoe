package com.utils;

import android.util.*;

public class Logger {
    private static final boolean LOG_ENABLE = true;
    private static final String TAG = "XOGameLogs";
    private static final String TAG_ERROR = "MyLogsError";
    private static final String LOG_UNICK = "X-O";


    public static void logD(String tag, String message) {
        if (LOG_ENABLE) {
            Log.d(tag, message);
        }
    }

    public static void logE(String tag, String message) {
        if (LOG_ENABLE) {
            Log.e(tag, message);
        }
    }


    public static void printLog(String s) {

        if (LOG_ENABLE)
            Log.d(TAG, s);
    }

    public static void printError(String s) {

        if (LOG_ENABLE)
            Log.e(TAG_ERROR, s);
    }


}
