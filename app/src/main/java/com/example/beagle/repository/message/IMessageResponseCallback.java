package com.example.beagle.repository.message;

import com.example.beagle.model.Message;

import java.util.List;

public interface IMessageResponseCallback {
    void onSuccessReadFromRemote(List<Message> messageList, long conversationId);
    void onFailureReadFromRemote(Exception exception);
    void onSuccessReadFromLocal(List<Message> messageList);
    void onFailureReadFromLocal(Exception exception);

    void onSuccessUpdateFromLocal(List<Message> messageList);
    void onFailureUpdateFromLocal(Exception exception);


    void onSuccessWriteFromRemote(Message message);
    void onFailureWriteFromRemote(String exception);
    void onSuccessWriteFromLocal(List<Message> messageList);
    void onFailureWriteFromLocal(Exception exception);

}
