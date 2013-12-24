package com.config;

import android.R.string;

public final class Config {
	private Config (){}
    public static final boolean GLOBAL = true;
	public static final String HOST = GLOBAL ? "81.30.156.50"
			: "192.168.1.106";
	public static final int PORT = 19999;
}
