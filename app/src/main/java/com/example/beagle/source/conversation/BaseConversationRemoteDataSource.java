package com.example.beagle.source.conversation;

import com.example.beagle.model.Conversation;
import com.example.beagle.repository.conversation.IConversationResponseCallback;

public abstract class BaseConversationRemoteDataSource {

    protected IConversationResponseCallback conversationCallback;

    public void setConversationCallback(IConversationResponseCallback conversationCallback) {
        this.conversationCallback = conversationCallback;
    }

    public abstract void getConversations(long petId);

    public abstract void insertConversation(Conversation conversation, long petId);
}
