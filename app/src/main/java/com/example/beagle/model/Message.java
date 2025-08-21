package com.example.beagle.model;

import java.time.Instant;

public class Message {
    private String conversationId; // chiave esterna
    private int seq; // numero crescente
    private Instant ts; // timestamp
    private boolean fromUser; // true se il messaggio è dell'utente
    private String content;

    public Message() {
    }

    public Message(String content, boolean fromUser) {
        this.content = content;
        this.fromUser = fromUser;
        this.ts = Instant.now();
    }


    public String getConversationId() {
        return conversationId;
    }
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public int getSeq() {
        return seq;
    }
    public void setSeq(int seq) {
        this.seq = seq;
    }

    public Instant getTs() {
        return ts;
    }
    public void setTs(Instant ts) {
        this.ts = ts;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public boolean getFromUser() {
        return fromUser;
    }
    public void setFromUser(boolean fromUser) {
        this.fromUser = fromUser;
    }

    public String getMessage() {
    return content;
    }

    public void setMessage(String message) {
        this.content = message;
    }
}
