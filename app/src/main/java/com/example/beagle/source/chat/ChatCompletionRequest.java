package com.example.beagle.source.chat;

import java.util.List;

public class ChatCompletionRequest {
    private String model;
    private List<APIMessage> messages;

    public ChatCompletionRequest() {
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<APIMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<APIMessage> messages) {
        this.messages = messages;
    }
}
