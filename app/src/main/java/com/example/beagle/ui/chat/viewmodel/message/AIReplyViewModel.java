package com.example.beagle.ui.chat.viewmodel.message;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.example.beagle.model.Result;
import com.example.beagle.repository.message.MessageRepository;

public class AIReplyViewModel extends ViewModel {
    private final MessageRepository messageRepository;
    private MutableLiveData<Result> messageAILiveData;

    public AIReplyViewModel(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }


    public MutableLiveData<Result> getAIReply(long conversationId, int seq) {
        messageAILiveData = messageRepository.getAIReply(conversationId, seq);
        Log.d("test", "Returning messageAILiveData");
        return messageAILiveData;
    }
}
