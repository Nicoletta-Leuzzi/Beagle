package com.example.beagle.model;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    foreignKeys = {
            @ForeignKey( // deferibile se serve inserire prima la convo e poi il pet nella stessa transazione
                    entity = Pet.class,
                    parentColumns = "petId",
                    childColumns = "petId",
                    onDelete = ForeignKey.CASCADE,
                    onUpdate = ForeignKey.CASCADE,
                    deferred = true
            )
    },
    indices = {@Index("petId") }

)
public class Conversation {
    @PrimaryKey(autoGenerate = true)// TODO: NOTA, autoGenerate NON funge con foreign key per qualche ragione
    private long conversationId;
    private String conversationTitle;
    private long idToken;
    private long petId;
    private long createdAt; // timestamp in UTC


    @Ignore
    public Conversation() {
    }


    public Conversation(long petId) {
        this.petId = petId;
        this.createdAt = System.currentTimeMillis();
    }

    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
        // TODO: da generare
    }

    public String getConversationTitle() {
        return conversationTitle;
    }

    public void setConversationTitle(String conversationTitle) {
        this.conversationTitle = conversationTitle;
    }

    public long getIdToken() {
        return idToken;
    }

    public void setIdToken(long idToken) {
        this.idToken = idToken;
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

}