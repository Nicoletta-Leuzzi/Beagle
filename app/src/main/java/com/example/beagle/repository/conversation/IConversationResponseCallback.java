package com.example.beagle.repository.conversation;

import com.example.beagle.model.Conversation;

import java.util.List;

public interface IConversationResponseCallback {
    void onSuccessFromRemote();
    void onFailureFromRemote(Exception exception);
    void onSuccessFromLocal(List<Conversation> conversationList);
    void onFailureFromLocal(Exception exception);


}
