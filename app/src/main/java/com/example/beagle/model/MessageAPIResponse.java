package com.example.beagle.model;

import java.util.List;

public class MessageAPIResponse {
    private String status;
    private int totalResults;
    private List<Message> messages;

    public MessageAPIResponse(){}

    public MessageAPIResponse(List<Message> messages) {
        this.messages = messages;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
