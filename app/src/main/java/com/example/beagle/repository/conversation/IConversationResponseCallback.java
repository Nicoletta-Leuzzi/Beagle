package com.example.beagle.repository.conversation;

import com.example.beagle.model.Conversation;

import java.util.List;

public interface IConversationResponseCallback {

    void onSuccessFromLocal(List<Conversation> conversationList);
    void onSuccessReadFromRemote(List<Conversation> conversationList, long petId);
    void onSuccessWriteFromLocal(Conversation conversation);
    void onSuccessDeleteFromRemote();

    void onFailureReadFromRemote(Exception exception);
    void onFailureDeleteFromRemote(Exception exception);

}
