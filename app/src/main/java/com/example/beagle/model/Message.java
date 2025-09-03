package com.example.beagle.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

@Entity(
    primaryKeys = {"conversationId", "seq"},
    foreignKeys = @ForeignKey(
            entity = Conversation.class,
            parentColumns = "conversationId",
            childColumns = "conversationId",
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
    )
)
public class Message {
    @NonNull
    private String conversationId; // chiave esterna
    private int seq; // numero crescente
    private long ts = 0L; // timestamp inizializzato
    private boolean fromUser; // true se il messaggio è dell'utente
    private String content;

    // costruttore vuoto per firebase
    public Message() {
    }

    public Message(@NonNull String conversationId, int seq, long ts,
                   boolean fromUser, String content) {
        this.conversationId = conversationId;
        this.seq = seq;
        this.ts = ts;
        this.fromUser = fromUser;
        this.content = content;
        // ts verrà assegnato da Conversation.java per indicare il ts del messaggio inviato
    }

    // TODO: da togliere dopo aver fixato showMessage in ChatFragment
    @Ignore
    public Message(@NonNull String content, boolean fromUser) {
        this.content = content;
        this.fromUser = fromUser;
        // opzionale se hai un timestamp
        // this.timestamp = System.currentTimeMillis();
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

    public boolean isFromUser() {
        return fromUser;
    }
    public void setFromUser(boolean fromUser) {
        this.fromUser = fromUser;
    }
}
