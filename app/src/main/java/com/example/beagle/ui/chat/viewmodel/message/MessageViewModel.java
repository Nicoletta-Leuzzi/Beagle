package com.example.beagle.ui.chat.viewmodel.message;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.beagle.model.Message;
import com.example.beagle.model.Result;
import com.example.beagle.repository.message.MessageRepository;

public class MessageViewModel extends ViewModel {

    private final MessageRepository messageRepository;
    private MutableLiveData<Result> messageListLiveData;
    //private MutableLiveData<Result> messageLiveData;

    public MessageViewModel(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }


    public MutableLiveData<Result> getMessages(long conversationId, boolean fromRemote) {
        messageListLiveData = messageRepository.fetchMessages(conversationId, fromRemote);
        return messageListLiveData;
    }

    /*
    public void addMessage2(Message message) {
        messageListLiveData = messageRepository.addMessage(message);
        return messageListLiveData.getValue();
    }

     */
    public MutableLiveData<Result> addMessage(Message message, long conversationId, long seq) {
        messageListLiveData = messageRepository.addMessage(message, conversationId, seq);
        return messageListLiveData;
    }

}
