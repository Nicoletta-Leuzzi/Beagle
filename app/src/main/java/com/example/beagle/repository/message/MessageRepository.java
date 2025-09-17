package com.example.beagle.repository.message;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.beagle.model.APIMessage;
import com.example.beagle.model.ChatCompletionResponse;
import com.example.beagle.model.Choice;
import com.example.beagle.model.Message;
import com.example.beagle.model.Result;
import com.example.beagle.source.message.BaseMessageAPIDataSource;
import com.example.beagle.source.message.BaseMessageLocalDataSource;
import com.example.beagle.source.message.BaseMessageRemoteDataSource;

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
    public void onFailureWriteFromRemote(Exception exception) {

    }

    @Override
    public void onSuccessWriteFromLocal(List<Message> allMessages, Message message, long conversationId, int seq) {
        //List<Message> messageList = new ArrayList<>();
        //messageList.add(message);
        Result.MessageReadSuccess result = new Result.MessageReadSuccess(allMessages);
        messageAddedLiveData.postValue(result);
        // Commentato perchè la lettura creava problemi con il MockAPI
        //allMessagesMutableLiveData.postValue(result);
        messageRemoteDataSource.insertMessage(message, conversationId, seq);
    }

    @Override
    public void onFailureWriteFromLocal(Exception exception) {

    }



    // DA CAMBIARE IL PARAMETRO CON LA RISPOSTA DELL'AI
    @Override
    public void onSuccessFromAPI(ChatCompletionResponse response, long conversationId, int seq) {

        // Verifica che la risposta sia valida
        if (response == null) {
            messageAILiveData.postValue(new Result.Error("Null response from API"));
            return;
        }

        // Recupera APIMessage e trasformalo in oggetto Message
        List<Choice> choices = response.getChoices();
        if (choices == null || choices.isEmpty()) {
            messageAILiveData.postValue(new Result.Error("No choices in AI response"));
            return;
        }

        APIMessage aiMsg;

        Choice choice = choices.get(0);
        if (choice != null) {
            aiMsg = choice.getMessage();
        } else aiMsg = null;

        if (aiMsg == null || aiMsg.getContent() == null) {
            messageAILiveData.postValue(new Result.Error("No message in choice 0"));
            return;
        }

        Message message = new Message(aiMsg, conversationId, seq);
        messageLocalDataSource.insertAIMessage(message, conversationId);
    }

    public void onSuccessFetchFromAPI(ChatCompletionResponse response, long conversationId, int seq) {

    // todo togliere?

        // Infine chiama qualcosa (o addMessage, o le chiamate dentro, per poi forse tornare qualche livedata?)
    }

    @Override
    public void onSuccessWriteAIFromLocal(List<Message> messageList, Message message) {
        Result.MessageReadSuccess result = new Result.MessageReadSuccess(messageList);
        messageAILiveData.postValue(result);
        allMessagesMutableLiveData.postValue(result);
        messageRemoteDataSource.insertMessage(message, message.getConversationId(), message.getSeq());
    }
}
