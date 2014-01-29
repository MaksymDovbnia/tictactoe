package com.bigtictactoeonlinegame.chat;

/**
 * Date: 06.09.13
 *
 * @author Maksym Dovbnia (maksym.dovbnia@gmail.com)
 */
public interface IChatActionNotification {


    public void actionSendChatMessage(ChatMessage chatMessage);

    public String getPlayerName();


}
