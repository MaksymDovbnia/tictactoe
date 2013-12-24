package com.game.chat;

/**
 * Created by Maksym on 09.11.13.
 */
public interface ChatActionNotification {


    public void actionSendChatMessage(ChatMessage chatMessage);
    public String getPlayerName();


}
