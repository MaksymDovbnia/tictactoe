package com.game.gamefield;

import com.game.chat.ChatMessage;

/**
 * Created by Maksym on 9/1/13.
 */
public interface GameFieldActivityAction {

    public void showWonPopup(String wonPlayerName);
    public void opponentExitFromGame();
    public void connectionToServerLost();
    public void receivedChatMessage(ChatMessage chatMessage);


}
