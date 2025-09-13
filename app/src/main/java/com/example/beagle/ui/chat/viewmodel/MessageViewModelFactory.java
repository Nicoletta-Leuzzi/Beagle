package com.example.beagle.ui.chat.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.beagle.repository.message.MessageRepository;

public class MessageViewModelFactory implements ViewModelProvider.Factory {

    private final MessageRepository messageRepository;

    public MessageViewModelFactory(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MessageViewModel(messageRepository);
    }
}
