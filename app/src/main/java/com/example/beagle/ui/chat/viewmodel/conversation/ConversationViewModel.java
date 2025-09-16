package com.example.beagle.ui.chat.viewmodel.conversation;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.beagle.model.Conversation;
import com.example.beagle.model.Result;
import com.example.beagle.repository.conversation.ConversationRepository;

public class ConversationViewModel extends ViewModel {

    private final ConversationRepository conversationRepository;
    private MutableLiveData<Result> conversationListLiveData;

    public ConversationViewModel(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    public MutableLiveData<Result> getConversations(long petId, boolean fromRemote) {
        conversationListLiveData = conversationRepository.fetchConversations(petId, fromRemote);
        return conversationListLiveData;
    }

    public void addConversation(Conversation conversation, long petId) {
        conversationRepository.addConversation(conversation, petId);
    }

    public void deleteConversation(Conversation conversation, long petId) {
        conversationRepository.deleteConversation(conversation, petId);
    }

}
