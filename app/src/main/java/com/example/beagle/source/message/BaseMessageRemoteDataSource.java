package com.example.beagle.source.message;

import com.example.beagle.model.Message;
import com.example.beagle.repository.message.IMessageResponseCallback;

public abstract class BaseMessageRemoteDataSource {
    protected IMessageResponseCallback messageCallback;

    public void setMessageCallback(IMessageResponseCallback messageCallback) {
        this.messageCallback = messageCallback;
    }

    public abstract void getMessages(long conversationId);

    public abstract void insertMessage(Message message, long conversationId, long seq);
}
