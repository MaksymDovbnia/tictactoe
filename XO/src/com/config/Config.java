package com.config;

import com.google.android.gms.drive.internal.f;

public final class Config {
    private Config() {
    }

    public static final boolean GLOBAL = false;
    public static final String HOST = GLOBAL ? "81.30.156.50"
            : "192.168.1.103";
    public static final int PORT = 19999;

    public static final boolean IS_ADD_ENABLE = true;
    public static final float APP_VERSION = 0.10f;
}
