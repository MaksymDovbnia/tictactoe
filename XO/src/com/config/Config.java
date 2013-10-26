package com.config;

import android.R.string;

public interface Config {
	public static final boolean GLOBAL = false;
	public static final String HOST = GLOBAL ? "178.150.67.156"
			: "192.168.1.105";
	public static final int PORT = 19999;
}
