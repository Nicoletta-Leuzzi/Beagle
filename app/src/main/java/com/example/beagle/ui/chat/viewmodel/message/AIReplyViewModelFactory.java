package com.example.beagle.ui.chat.viewmodel.message;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.beagle.repository.message.MessageRepository;

public class AIReplyViewModelFactory implements ViewModelProvider.Factory {

    private final MessageRepository messageRepository;

    public AIReplyViewModelFactory(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AIReplyViewModel(messageRepository);
    }
}
