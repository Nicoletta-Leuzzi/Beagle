package com.example.beagle.model;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

@Entity(
    /*foreignKeys = {
            @ForeignKey(
                    entity = User.class,
                    parentColumns = "idToken",
                    childColumns = "idToken",
                    onDelete = ForeignKey.CASCADE,
                    onUpdate = ForeignKey.CASCADE
            ),
            @ForeignKey( // deferibile se ti serve inserire prima la convo e poi il pet nella stessa transazione
                    entity = Pet.class,
                    parentColumns = "petId",
                    childColumns = "petId",
                    onDelete = ForeignKey.CASCADE,
                    onUpdate = ForeignKey.CASCADE,
                    deferred = true
            )
    },
    indices = { @Index("idToken"), @Index("petId") }

     */
)
public class Conversation {
    //@PrimaryKey(autoGenerate = true)// TODO: NOTA, autoGenerate NON funge con foreign key per qualche ragione
    @PrimaryKey ()
    private long conversationId;
    private long idToken;
    private long petId;
    private long createdAt; // timestamp in UTC

    //TODO: messaggi utente da "collegare"
    //private List<Message> messages;

    @Ignore
    public Conversation() {
        //this.messages = new ArrayList<>();
        //this.createdAt = System.currentTimeMillis();
    }

    //public Conversation(long conversationId, long petId, long createdAt) {
    public Conversation(long conversationId, long petId) {
        //this.conversationId = conversationId;
        this.conversationId = conversationId; // TODO: rimuovere
        this.petId = petId;
        this.createdAt = System.currentTimeMillis();
        //this.messages = new ArrayList<>();
    }

    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
        // TODO: da generare
    }

    public long getPetId() {
        return petId;
    }

    public void setPetId(long petId) {
        this.petId = petId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getIdToken() {
        return idToken;
    }

    public void setIdToken(long idToken) {
        this.idToken = idToken;
    }


    // sostituisce tutti i messaggi: per sincronizzazione e caricamento dati
    /*
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
        // TODO: va nella classe Message
        if (m.getTs() == 0L) { // se non è presente
            m.setTs(System.currentTimeMillis());
        }

        // assegna numero progressivo
        if (m.getSeq() <= 0) {
            m.setSeq(getNextSeq());
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

     */
}