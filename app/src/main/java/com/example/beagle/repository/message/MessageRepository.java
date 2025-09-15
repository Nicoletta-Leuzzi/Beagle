package com.example.beagle.repository.message;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.beagle.model.Message;
import com.example.beagle.model.MessageAPIResponse;
import com.example.beagle.model.Result;
import com.example.beagle.source.message.BaseMessageLocalDataSource;
import com.example.beagle.source.message.BaseMessageRemoteDataSource;

import java.util.List;

public class MessageRepository implements IMessageResponseCallback {
    private static final String TAG = MessageRepository.class.getSimpleName();

    private final MutableLiveData<Result> allMessagesMutableLiveData;
    private final BaseMessageRemoteDataSource messageRemoteDataSource;
    private final BaseMessageLocalDataSource messageLocalDataSource;

    public MessageRepository(BaseMessageRemoteDataSource messageRemoteDataSource,
                             BaseMessageLocalDataSource messageLocalDataSource) {

        allMessagesMutableLiveData = new MutableLiveData<>();
        this.messageRemoteDataSource = messageRemoteDataSource;
        this.messageLocalDataSource = messageLocalDataSource;
        this.messageRemoteDataSource.setMessageCallback(this);
        this.messageLocalDataSource.setMessageCallback(this);
    }

    public MutableLiveData<Result> fetchMessages(long conversationId, boolean fromRemote) {
        Log.d("Test", "MESSAGE REPOSITORY " + fromRemote);
        if (fromRemote) {
            messageRemoteDataSource.getMessages(conversationId);
        } else {
            messageLocalDataSource.getMessages(conversationId);
        }
        return  allMessagesMutableLiveData;
    }

    public MutableLiveData<Result> addMessage(Message message, long conversationId, long seq) {
        messageLocalDataSource.insertMessage(message, conversationId);
        // TODO: Come faccio ad inserire un messaggio sia su locale, sia su firebase?
        messageRemoteDataSource.insertMessage(message, conversationId, seq);
        return allMessagesMutableLiveData;
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
        Result.MessageSuccess result = new Result.MessageSuccess(messageList);
        allMessagesMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureReadFromLocal(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        allMessagesMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessUpdateFromLocal(List<Message> messageList) {
        Result.MessageSuccess result = new Result.MessageSuccess(messageList);
        allMessagesMutableLiveData.postValue(result);
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
    public void onSuccessWriteFromLocal(List<Message> messageList) {
        Result.MessageSuccess result = new Result.MessageSuccess(messageList);
        allMessagesMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureWriteFromLocal(Exception exception) {

    }


}
