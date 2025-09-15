package com.example.beagle.source.message;

import com.example.beagle.model.Message;
import com.example.beagle.repository.message.IMessageResponseCallback;

import java.util.List;

public abstract class BaseMessageLocalDataSource {
    protected IMessageResponseCallback messageCallback;

    public void setMessageCallback(IMessageResponseCallback messageCallback) {
        this.messageCallback = messageCallback;
    }

    public abstract void getMessages(long conversationId);

    public abstract void insertMessages(List<Message> messageList);

    public abstract void insertMessage(Message message, long conversationId);

    public abstract void updateMessages(List<Message> messageList, long conversationId);
}
