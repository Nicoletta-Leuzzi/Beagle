package com.example.beagle.source.message;

import android.util.Log;

import com.example.beagle.database.DataRoomDatabase;
import com.example.beagle.database.MessageDAO;
import com.example.beagle.model.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageLocalDataSource extends BaseMessageLocalDataSource {

    private final MessageDAO messageDAO;


    public MessageLocalDataSource(DataRoomDatabase dataRoomDatabase) {
        this.messageDAO = dataRoomDatabase.messageDao();
    }

    @Override
    public void getMessages(long conversationId) {
        DataRoomDatabase.databaseWriteExecutor.execute(() -> {
            messageCallback.onSuccessReadFromLocal(messageDAO.getMessages(conversationId));
        });
    }


    @Override
    public void insertMessages(List<Message> messageList) {
        DataRoomDatabase.databaseWriteExecutor.execute(() -> {
            messageDAO.insertAll(messageList);
            List<Message> updatedMessageList = messageDAO.getAll();

            messageCallback.onSuccessReadFromLocal(updatedMessageList);
        });
    }


    @Override
    public void insertMessage(Message message, long conversationId) {
        DataRoomDatabase.databaseWriteExecutor.execute(() -> {
            messageDAO.insert(message);
            messageCallback.onSuccessWriteFromLocal(message, conversationId, message.getSeq());
        });
    }

    @Override
    public void insertAIMessage(Message message, long conversationId) {
        DataRoomDatabase.databaseWriteExecutor.execute(() -> {
            messageDAO.insert(message);
            List<Message> messageAddedList = new ArrayList<>();
            messageAddedList.addAll(messageDAO.getMessages(conversationId));
            messageCallback.onSuccessWriteAIFromLocal(messageAddedList);
        });
    }



    @Override
    public void updateMessages(List<Message> messageList, long conversationId) {
        DataRoomDatabase.databaseWriteExecutor.execute(() -> {
            messageDAO.updateMessages(messageList);
            List<Message> updatedMessageList = messageDAO.getMessages(conversationId);
            messageCallback.onSuccessUpdateFromLocal(updatedMessageList);
        });
    }
}
