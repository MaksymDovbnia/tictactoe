package com.game.chat;

/**
 * Created by Maksym on 09.11.13.
 */
public interface ChatActionNotification {


    public void actionSendMessage(ChatMessage chatMessage);
    public String getPlayerName();


}
