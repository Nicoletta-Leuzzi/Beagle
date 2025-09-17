package com.example.beagle.repository.message;

import com.example.beagle.model.ChatCompletionResponse;
import com.example.beagle.model.Message;

import java.util.List;

public interface IMessageResponseCallback {

    void onSuccessFromAPI(ChatCompletionResponse response, long conversationId, int seq);
    void onSuccessReadFromRemote(List<Message> messageList, long conversationId);
    void onSuccessReadFromLocal(List<Message> messageList);

    void onSuccessWriteFromRemote(Message message);
    void onSuccessWriteAIFromLocal(List<Message> allMessages, Message message);
    void onSuccessWriteFromLocal(List<Message> allMessages, Message message, long conversationId, int seq);
    void onSuccessUpdateFromLocal(List<Message> messageList);

    void onFailureReadFromRemote(Exception exception);
    void onFailureReadFromLocal(Exception exception);
    void onFailureWriteFromRemote(Exception exception);
    void onFailureWriteFromLocal(Exception exception);
    void onFailureUpdateFromLocal(Exception exception);

}
