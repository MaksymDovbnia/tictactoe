package com.utils;

import android.R.string;
import android.util.Log;

public class Loger {
	private static boolean canPrintLog = true;
	private static final String TAG = "MyLogs";
    private static final String TAG_ERROR = "MyLogsError";

	public static void printLog(String s) {

		if (canPrintLog)
			Log.d(TAG, s);
	}

    public static void printEror(String s) {

        if (canPrintLog)
            Log.e(TAG_ERROR, s);
    }




}
