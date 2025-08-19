package com.example.beagle.model;

public class Message {
    private String message;
    private boolean user;

    public Message() {
    }

    public Message(String message, boolean user) {
        this.message = message;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public boolean getUser() {
        return user;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
