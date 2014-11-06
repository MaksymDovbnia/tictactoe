package com.utils;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * Date: 10.03.13
 * Time: 20:22
 *
 * @author Maksym Dovbnia (maksym.dovbnia@gmail.com)
 */
public final class Mathematics {
    private static final int START_R_WIN = 10;
    private static final int START_R_LOST = -5;

    private Mathematics() {
    }

    public static int calculateWinPoints(int ratingWinPlayer, int ratingLostPlayer) {

        return START_R_WIN + ratingLostPlayer / 100;


    }

    public static long calculateWinPoints(long ratingWinPlayer, long ratingLostPlayer) {
        return START_R_WIN + ratingLostPlayer / 50;


    }

    public static int calculateLostPoints(int ratingWinPlayer, int ratingLostPlayer) {
        return START_R_LOST;
    }

    public static long calculateLostPoints(long ratingWinPlayer, long ratingLostPlayer) {
        return START_R_LOST;
    }


}
