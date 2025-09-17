package com.example.beagle.source.message;

import com.example.beagle.repository.message.IMessageResponseCallback;

public abstract class BaseMessageAPIDataSource {
    protected IMessageResponseCallback messageCallback;

    public void setMessageCallback(IMessageResponseCallback messageCallback) {
        this.messageCallback = messageCallback;
    }

    // DOVREBBE AVERE COME PARAMETRO QUELLO CHE PASSIAMO ALL'AI
    public abstract void getReply(long conversationId, int seq);
}
