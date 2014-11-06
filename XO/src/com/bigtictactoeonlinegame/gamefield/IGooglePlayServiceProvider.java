package com.bigtictactoeonlinegame.gamefield;

/**
 * Created by Maksym on 21.07.2014.
 */
public interface IGooglePlayServiceProvider {
    public void wonOneGameVsAndroid();

    public void winOneGameViaBluetooth();

    public void winOneGameViaOnline(long playerScoreInPS, long opponentScorePS);

    public void lostOneGameViaOnline(long playerScoreInPS, long opponentScorePS);


}
