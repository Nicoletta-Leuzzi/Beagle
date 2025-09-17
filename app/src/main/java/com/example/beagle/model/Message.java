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
            onUpdate = ForeignKey.CASCADE)
)
public class Message {
    private long conversationId; // chiave esterna
    private int seq; // numero crescente
    private long ts = 0L; // timestamp inizializzato
    private boolean fromUser; // true se il messaggio è dell'utente
    private String content;

    // costruttore vuoto per firebase
    @Ignore
    public Message() {
    }

    public Message(long conversationId, int seq,
                   boolean fromUser, String content) {
        this.conversationId = conversationId;
        this.seq = seq;
        this.ts = System.currentTimeMillis();
        this.fromUser = fromUser;
        this.content = content;
    }

    // costruttore per il messaggio dell'AI
    public Message(APIMessage AImessage, long conversationId, int seq) {
        this.conversationId = conversationId;
        this.seq = seq;
        this.ts = System.currentTimeMillis();
        this.content = AImessage.getContent();
        this.fromUser = false;
    }


    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(@NonNull long conversationId) {
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
    public boolean isFromUser() {
        return fromUser;
    }
    public void setFromUser(boolean fromUser) {
        this.fromUser = fromUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
