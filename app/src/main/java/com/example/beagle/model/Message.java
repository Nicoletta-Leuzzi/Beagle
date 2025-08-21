package com.example.beagle.model;

public class Message {
    private String message;
    private boolean isUser;

    public Message() {
    }

    public Message(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }

    // TODO: Setter da rimuovere perchè non neccessario penso
    public void setMessage(String message) {
        this.message = message;
    }
}
