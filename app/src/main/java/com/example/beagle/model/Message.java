package com.example.beagle.model;

public class Message {
    private String messageId;
    private String conversationId; // chiave esterna
    private int seq; // numero crescente
    private long ts = 0L; // timestamp inizializzato
    private boolean fromUser; // true se il messaggio è dell'utente
    private String content;

    // costruttore vuoto per firebase
    public Message() {
    }

    public Message(String content, boolean fromUser) {
        this.content = content;
        this.fromUser = fromUser;
        // ts verrà assegnato da Conversation.java per indicare il ts del messaggio inviato
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
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

    public long getTs() {
        return ts;
    }
    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getMessageContent() {
        return content;
    }
    public void setMessageContent(String content) {
        this.content = content;
    }

    public boolean getFromUser() {
        return fromUser;
    }
    public void setFromUser(boolean fromUser) {
        this.fromUser = fromUser;
    }
}
