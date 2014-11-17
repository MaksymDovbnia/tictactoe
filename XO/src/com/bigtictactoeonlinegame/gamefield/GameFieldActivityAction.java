package com.bigtictactoeonlinegame.gamefield;

import com.bigtictactoeonlinegame.chat.ChatMessage;

/**
 * Created by Maksym on 9/1/13.
 */
public interface GameFieldActivityAction {

    public void showWonPopup(boolean IsPlayerWin);

    public void opponentExitFromGame();

    public void connectionToServerLost();

    public void receivedChatMessage(ChatMessage chatMessage);

    public IGooglePlayServiceProvider getPlayServiceProvider();

    public GameScoreSettable getGameScoreSettable();



}
