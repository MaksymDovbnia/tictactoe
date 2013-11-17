package com.game.chat;

/**
 * Created by Maksym on 09.11.13.
 */
public class ChatMessage {
private String message;
    private String sender;


    public ChatMessage(String message, String sender) {
        this.message = message;
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
