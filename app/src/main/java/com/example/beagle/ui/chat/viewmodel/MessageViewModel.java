package com.example.beagle.ui.chat.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.beagle.model.Message;
import com.example.beagle.model.Result;
import com.example.beagle.repository.message.MessageRepository;

public class MessageViewModel extends ViewModel {

    private final MessageRepository messageRepository;
    private final int page;
    private MutableLiveData<Result> messageListLiveData;

    public MessageViewModel(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
        this.page = 1;
    }

    public MutableLiveData<Result> getMessages(long conversationId, boolean fromRemote) {
        fetchMessages(conversationId, fromRemote);
        return messageListLiveData;
    }

    public void addMessage(Message message) {
        messageRepository.addMessage(message);
    }

    private void fetchMessages(long conversationId, boolean fromRemote) {
        messageListLiveData = messageRepository.fetchMessages(conversationId, fromRemote);
    }
}
