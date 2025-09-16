package com.example.beagle.repository.message;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.beagle.model.Message;
import com.example.beagle.model.MessageAPIResponse;
import com.example.beagle.model.Result;
import com.example.beagle.source.message.BaseMessageAPIDataSource;
import com.example.beagle.source.message.BaseMessageLocalDataSource;
import com.example.beagle.source.message.BaseMessageRemoteDataSource;

import java.util.ArrayList;
import java.util.List;

public class MessageRepository implements IMessageResponseCallback {
    private static final String TAG = MessageRepository.class.getSimpleName();

    private final MutableLiveData<Result> allMessagesMutableLiveData;
    private final MutableLiveData<Result> messageAddedLiveData;
    private final MutableLiveData<Result> messageAILiveData;
    private final BaseMessageRemoteDataSource messageRemoteDataSource;
    private final BaseMessageLocalDataSource messageLocalDataSource;
    private final BaseMessageAPIDataSource messageAPIDataSource;

    public MessageRepository(BaseMessageRemoteDataSource messageRemoteDataSource,
                             BaseMessageLocalDataSource messageLocalDataSource,
                             BaseMessageAPIDataSource messageAPIDataSource) {

        allMessagesMutableLiveData = new MutableLiveData<>();
        messageAddedLiveData = new MutableLiveData<>();
        messageAILiveData = new MutableLiveData<>();
        this.messageRemoteDataSource = messageRemoteDataSource;
        this.messageLocalDataSource = messageLocalDataSource;
        this.messageAPIDataSource = messageAPIDataSource;
        this.messageRemoteDataSource.setMessageCallback(this);
        this.messageLocalDataSource.setMessageCallback(this);
        this.messageAPIDataSource.setMessageCallback(this);
    }

    public MutableLiveData<Result> fetchMessages(long conversationId, boolean fromRemote) {
        if (fromRemote) {
            messageRemoteDataSource.getMessages(conversationId);
        } else {
            messageLocalDataSource.getMessages(conversationId);
        }
        return  allMessagesMutableLiveData;
    }

    public MutableLiveData<Result> addMessage(Message message, long conversationId, int seq) {
        Log.d("test", "ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDMES");
        messageLocalDataSource.insertMessage(message, conversationId);
        // TODO: Come faccio ad inserire un messaggio sia su locale, sia su firebase?
        //messageRemoteDataSource.insertMessage(message, conversationId, seq);
        return messageAddedLiveData;
    }

    public MutableLiveData<Result> getAIReply(long conversationId, int seq) {
        messageAPIDataSource.getReply(conversationId, seq);
        return messageAILiveData;
    }


    @Override
    public void onSuccessReadFromRemote(List<Message> messageList, long conversationId) {
        messageLocalDataSource.updateMessages(messageList, conversationId);
    }

    @Override
    public void onFailureReadFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        allMessagesMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessReadFromLocal(List<Message> messageList) {
        Result.MessageReadSuccess result = new Result.MessageReadSuccess(messageList);
        allMessagesMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureReadFromLocal(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        allMessagesMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessUpdateFromLocal(List<Message> messageList) {
        Result.MessageReadSuccess result = new Result.MessageReadSuccess(messageList);
        messageAddedLiveData.postValue(result);
    }

    @Override
    public void onFailureUpdateFromLocal(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        allMessagesMutableLiveData.postValue(result);
    }


    @Override
    public void onSuccessWriteFromRemote(Message message) {

    }

    @Override
    public void onFailureWriteFromRemote(String exception) {

    }

    @Override
    public void onSuccessWriteFromLocal(List<Message> allMessages, Message message, long conversationId, int seq) {
        //List<Message> messageList = new ArrayList<>();
        //messageList.add(message);
        Result.MessageReadSuccess result = new Result.MessageReadSuccess(allMessages);
        messageAddedLiveData.postValue(result);
        allMessagesMutableLiveData.postValue(result);
        messageRemoteDataSource.insertMessage(message, conversationId, seq);



        //allMessagesMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureWriteFromLocal(Exception exception) {

    }



    // DA CAMBIARE IL PARAMETRO CON LA RISPOSTA DELL'AI
    @Override
    public void onSuccessFromAPI(String REPLY_WIP, long conversatioId, int seq) {
        Message AIReply = new Message(conversatioId, seq, false, REPLY_WIP);


        messageLocalDataSource.insertAIMessage(AIReply, conversatioId);
        //List<Message> test = new ArrayList<>();
        //test.add(AIReply);
        //Result.MessageReadSuccess result = new Result.MessageReadSuccess(test);
        //messageAILiveData.postValue(result);
        //allMessagesMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessWriteAIFromLocal(List<Message> messageList, Message message) {
        Result.MessageReadSuccess result = new Result.MessageReadSuccess(messageList);
        messageAILiveData.postValue(result);
        allMessagesMutableLiveData.postValue(result);
        messageRemoteDataSource.insertMessage(message, message.getConversationId(), message.getSeq());
    }
}
