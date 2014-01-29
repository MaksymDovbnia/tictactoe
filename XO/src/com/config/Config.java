package com.config;

public final class Config {
    private Config() {
    }

    public static final boolean GLOBAL = true;
    public static final String HOST = GLOBAL ? "81.30.156.50"
            : "192.168.1.104";
    public static final int PORT = 19999;

    public static final boolean IS_ADD_ENABLE = true;
}
