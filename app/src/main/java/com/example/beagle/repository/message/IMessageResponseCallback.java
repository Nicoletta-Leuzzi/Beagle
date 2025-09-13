package com.example.beagle.repository.message;

import com.example.beagle.model.Message;

import java.util.List;

public interface IMessageResponseCallback {
    void onSuccessFromRemote(List<Message> messageList);
    void onFailureFromRemote(String exception);
    void onSuccessFromLocal(List<Message> messageList);
    void onFailureFromLocal(Exception exception);
    void onMessageAdded(Message message);
}
