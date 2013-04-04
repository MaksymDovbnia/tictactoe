package com.utils;

import android.R.string;
import android.util.Log;

public class Loger {
	private static boolean canPrintLog = true;
	private static final String TAG = "MyLogs";

	public static void printLog(String s) {

		if (canPrintLog)
			Log.d(TAG, s);
	}

}
