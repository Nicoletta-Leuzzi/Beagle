package com.example.beagle.repository.conversation;

import com.example.beagle.model.Conversation;

import java.util.List;

public interface IConversationResponseCallback {
    void onSuccessFromRemote();
    void onFailureFromRemote(Exception exception);
    void onSuccessReadFromRemote(List<Conversation> conversationList, long petId);
    void onFailureReadFromRemote(Exception exception);
    void onSuccessFromLocal(List<Conversation> conversationList);
    void onFailureFromLocal(Exception exception);
    void onSuccessDeleteFromRemote();
    void onFailureDeleteFromRemote(Exception exception);


}
