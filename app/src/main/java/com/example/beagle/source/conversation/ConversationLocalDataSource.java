package com.example.beagle.source.conversation;

import com.example.beagle.database.ConversationDAO;
import com.example.beagle.database.DataRoomDatabase;
import com.example.beagle.database.MessageDAO;
import com.example.beagle.model.Conversation;

import java.util.List;

public class ConversationLocalDataSource extends BaseConversationLocalDataSource {

    private final ConversationDAO conversationDAO;
    private final MessageDAO messageDAO;


    public ConversationLocalDataSource(DataRoomDatabase dataRoomDatabase) {
        this.conversationDAO = dataRoomDatabase.conversationDao();
        this.messageDAO = dataRoomDatabase.messageDao();
    }


    @Override
    public void getConversations(long petId) {
        DataRoomDatabase.databaseWriteExecutor.execute(() -> {
            conversationCallback.onSuccessFromLocal(conversationDAO.getConversations(petId));
        });
    }

    @Override
    public void insertConversation(Conversation conversation, long petId) {
        DataRoomDatabase.databaseWriteExecutor.execute(() -> {
            conversationDAO.insert(conversation);
            List<Conversation> conversationList = conversationDAO.getConversations(petId);
            conversationCallback.onSuccessFromLocal(conversationList);
        });
    }

    @Override
    public void insertConversations(List<Conversation> conversationList, long petId) {
        DataRoomDatabase.databaseWriteExecutor.execute(() -> {
            conversationDAO.insertAll(conversationList);
            List<Conversation> newConversationList = conversationDAO.getConversations(petId);
            conversationCallback.onSuccessFromLocal(newConversationList);
        });
    }

    @Override
    public void deleteConversation(Conversation conversation, long petId) {
        DataRoomDatabase.databaseWriteExecutor.execute(() -> {
            conversationDAO.delete(conversation);
            messageDAO.deleteByConversation(conversation.getConversationId());
            List<Conversation> newConversationList = conversationDAO.getConversations(petId);
            conversationCallback.onSuccessFromLocal(newConversationList);
        });
    }
}
