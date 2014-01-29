package com.bigtictactoeonlinegame.chat;

/**
 * Date: 06.09.13
 *
 * @author Maksym Dovbnia (maksym.dovbnia@gmail.com)
 */
public class ChatMessage {
    private String mMessage;
    private String mSender;


    public ChatMessage(String message, String sender) {
        mMessage = message;
        mSender = sender;
    }

    public String getSender() {
        return mSender;
    }

    public void setSender(String sender) {
        this.mSender = sender;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }
}
