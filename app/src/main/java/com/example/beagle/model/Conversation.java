package com.example.beagle.model;

import java.time.Instant;
import java.util.List;
import java.util.ArrayList;

public class Conversation {
    private String conversationId;
    //TODO: userId che manca in User
    private String petId;
    private Instant createdAt; // timestamp in UTC

    //TODO: messaggi utente da "collegare"
    private List<Message> messages;

    public Conversation() {
        this.messages = new ArrayList<>();
        this.createdAt = Instant.now();
    }

    public Conversation(String conversationId, String petId, Instant createdAt) {
        this.conversationId = conversationId;
        this.petId = petId;
        this.createdAt = createdAt;
        this.messages = new ArrayList<>();
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setMessages(List<Message> messages) {
        if (messages == null) {
            this.messages = new ArrayList<>();
        } else {
            this.messages.clear();
        }

        if (messages != null) {
            this.messages.addAll(messages);
        }
    }

    public List<Message> getMessages() {
        return messages == null ? new ArrayList<>() : messages;
    }


}