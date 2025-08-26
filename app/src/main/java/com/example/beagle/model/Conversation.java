package com.example.beagle.model;


import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Conversation {
    private String conversationId;
    //TODO: userId che manca in User
    private String petId;
    private long createdAt; // timestamp in UTC

    //TODO: messaggi utente da "collegare"
    private List<Message> messages;

    public Conversation() {
        this.messages = new ArrayList<>();
        this.createdAt = System.currentTimeMillis();
    }

    public Conversation(String conversationId, String petId, long createdAt) {
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
        // TODO: da generare
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    // sostituisce tutti i messaggi: per sincronizzazione e caricamento dati
    public void setMessages(List<Message> messages) {
        if (this.messages == null) {
            this.messages = new ArrayList<>();
        } else {
            this.messages.clear();
        }

        if (messages != null) {
            this.messages.addAll(messages);
        }
    }

    // restituisce una vista non modificabile dei messaggi
    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
        } 

    public Message addMessage(Message m) {
        if (m == null) return null;
        if (this.messages == null) this.messages = new ArrayList<>();

        // assegna conversationId
        if (m.getConversationId() == null) {
            m.setConversationId(this.conversationId);
        }

        // assegna timestamp
        if (m.getTs() == 0L) { // se non è presente
            m.setTs(System.currentTimeMillis());
        }

        // assegna numero progressivo
        if (m.getSeq() <= 0) {
            m.setSeq(getNextSeq());
        }

        // assegna messageId composto (conversationId + seq) se assente
        if ((m.getMessageId() == null || m.getMessageId().isEmpty()) && this.conversationId != null && m.getSeq() > 0) {
            m.setMessageId(buildCompositeId(this.conversationId, m.getSeq()));
        }

        // aggiunge il messaggio alla conversazione
        this.messages.add(m);
        
        return m;
    }

    // restituisce il numero progressivo successivo
    private int getNextSeq() {
        if (this.messages == null || this.messages.isEmpty()) return 1;
        return this.messages.size() + 1;
    }

    // costruisce un messageId composto (conversationId + seq)
    private static String buildCompositeId(String conversationId, int seq) {
        String padded = String.format("%09d", seq);
        return conversationId + "-" + padded;
    }
}