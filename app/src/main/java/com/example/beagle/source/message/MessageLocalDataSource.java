package com.example.beagle.source.message;

import com.example.beagle.database.DataRoomDatabase;
import com.example.beagle.database.MessageDAO;
import com.example.beagle.model.Message;

import java.util.List;

public class MessageLocalDataSource extends BaseMessageLocalDataSource {

    private final MessageDAO messageDAO;


    public MessageLocalDataSource(DataRoomDatabase dataRoomDatabase) {
        this.messageDAO = dataRoomDatabase.messageDao();
    }

    @Override
    public void getMessages(long conversationId) {
        DataRoomDatabase.databaseWriteExecutor.execute(() -> {
            messageCallback.onSuccessFromLocal(messageDAO.getMessages(conversationId));
        });
    }


    @Override
    public void insertMessages(List<Message> messageList) {
        DataRoomDatabase.databaseWriteExecutor.execute(() -> {
            messageDAO.insertAll(messageList);
            List<Message> updatedMessageList = messageDAO.getAll();

            messageCallback.onSuccessFromLocal(updatedMessageList);
        });
    }


    @Override
    public void insertMessage(Message message) {
        DataRoomDatabase.databaseWriteExecutor.execute(() -> {
            messageDAO.insert(message);
            Message messageAdded = messageDAO.getSingleMessage(message.getConversationId(), message.getSeq());
            messageCallback.onMessageAdded(messageAdded);
        });
    }
}
