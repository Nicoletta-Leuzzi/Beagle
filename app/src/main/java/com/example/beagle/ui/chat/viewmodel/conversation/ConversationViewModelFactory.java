package com.example.beagle.ui.chat.viewmodel.conversation;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.beagle.repository.conversation.ConversationRepository;

public class ConversationViewModelFactory implements ViewModelProvider.Factory {

    private final ConversationRepository conversationRepository;

    public ConversationViewModelFactory(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ConversationViewModel(conversationRepository);
    }
}
