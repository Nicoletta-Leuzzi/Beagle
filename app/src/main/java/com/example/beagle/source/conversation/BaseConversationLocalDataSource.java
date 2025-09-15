package com.example.beagle.source.conversation;

import com.example.beagle.model.Conversation;
import com.example.beagle.model.Pet;
import com.example.beagle.repository.conversation.IConversationResponseCallback;

import java.util.List;

public abstract class BaseConversationLocalDataSource {
    protected IConversationResponseCallback conversationCallback;

    public void setConversationCallback(IConversationResponseCallback conversationCallback) {
        this.conversationCallback = conversationCallback;
    }

    public abstract void getConversations(long petId);
    public abstract void insertConversations(List<Conversation> conversationList, long petId);
    public abstract void insertConversation(Conversation conversation, long petId);
    public abstract void deleteConversation(Conversation conversation, long petId);
}
