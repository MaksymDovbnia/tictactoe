package com.bluetooth;

import com.entity.OneMove;

/**
 * Date: 22.12.13
 * Time: 18:13
 *
 * @author Maksym Dovbnia (maksym.dovbnia@gmail.com)
 */
public interface IBluetoothGameListener {
    public void receivedNewChatMessage(String mesage);

    public void receivedNewOneMove(OneMove oneMove);

    public void startGame(String opponentName);

    public void playerExitFromGame();

    public void continueGame();
}
