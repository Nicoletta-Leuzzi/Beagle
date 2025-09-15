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
            Log.d("TIMING", "GETMESSAGES");
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
            Log.d("asd", "InsertLocalMessage");
            messageDAO.insert(message);
            List<Message> messageAddedList = new ArrayList<>();
            messageAddedList.addAll(messageDAO.getMessages(conversationId));
            messageCallback.onSuccessWriteFromLocal(messageAddedList);
        });
    }

    @Override
    public void updateMessages(List<Message> messageList, long conversationId) {
        DataRoomDatabase.databaseWriteExecutor.execute(() -> {
            messageDAO.updateMessages(messageList);
            Log.d("TIMING", "UPDATEMESSAGES");
            List<Message> updatedMessageList = messageDAO.getMessages(conversationId);
            messageCallback.onSuccessUpdateFromLocal(updatedMessageList);
        });
    }
}
