package com.example.beagle.repository.conversation;

import androidx.lifecycle.MutableLiveData;

import com.example.beagle.model.Conversation;
import com.example.beagle.model.Result;
import com.example.beagle.source.conversation.BaseConversationLocalDataSource;
import com.example.beagle.source.conversation.BaseConversationRemoteDataSource;

import java.util.ArrayList;
import java.util.List;

public class ConversationRepository implements IConversationResponseCallback {

    private final MutableLiveData<Result> conversationsMutableLiveData;
    private final BaseConversationRemoteDataSource conversationRemoteDataSource;
    private final BaseConversationLocalDataSource conversationLocalDataSource;

    public ConversationRepository(BaseConversationRemoteDataSource conversationRemoteDataSource,
                                  BaseConversationLocalDataSource conversationLocalDataSource) {
        this.conversationsMutableLiveData = new MutableLiveData<>();
        this.conversationRemoteDataSource = conversationRemoteDataSource;
        this.conversationLocalDataSource = conversationLocalDataSource;
        this.conversationRemoteDataSource.setConversationCallback(this);
        this.conversationLocalDataSource.setConversationCallback(this);
    }

    public MutableLiveData<Result> fetchConversations(long petId, boolean fromRemote) {
        if (fromRemote) {
            conversationRemoteDataSource.getConversations(petId);
        } else {
            conversationLocalDataSource.getConversations(petId);
        }
        return conversationsMutableLiveData;
    }

    public void addConversation(Conversation conversation, long petId){
        conversationLocalDataSource.insertConversation(conversation, petId);
    }

    @Override
    public void onSuccessFromRemote() {
        // TODO
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        conversationsMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromLocal(List<Conversation> conversationList) {
        Result.ConversationSuccess result = new Result.ConversationSuccess(conversationList);
        conversationsMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureFromLocal(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        conversationsMutableLiveData.postValue(result);
    }
}
