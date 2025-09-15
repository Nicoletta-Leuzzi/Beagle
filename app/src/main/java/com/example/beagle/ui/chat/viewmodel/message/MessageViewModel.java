package com.example.beagle.ui.chat.viewmodel.message;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.beagle.model.Message;
import com.example.beagle.model.Result;
import com.example.beagle.repository.message.MessageRepository;

public class MessageViewModel extends ViewModel {

    private final MessageRepository messageRepository;
    private MutableLiveData<Result> messageListLiveData;
    private MutableLiveData<Result> messageAddLiveData;
    private MutableLiveData<Result> messageAILiveData;

    public MessageViewModel(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }


    public MutableLiveData<Result> getMessages(long conversationId, boolean fromRemote) {
        messageListLiveData = messageRepository.fetchMessages(conversationId, fromRemote);
        Log.d("test", "Returning messageListLiveData");
        return messageListLiveData;
    }

    public MutableLiveData<Result> addMessage(Message message, long conversationId, int seq) {
        messageAddLiveData = messageRepository.addMessage(message, conversationId, seq);
        Log.d("test", "Returning messageAddLiveData");
        return messageAddLiveData;
    }

    public MutableLiveData<Result> getAIReply(long conversationId, int seq) {
        messageAILiveData = messageRepository.getAIReply(conversationId, seq);
        Log.d("test", "Returning messageAILiveData");
        return messageAILiveData;
    }

}
