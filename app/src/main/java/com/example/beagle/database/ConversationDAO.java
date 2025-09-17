package com.example.beagle.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.beagle.model.Conversation;

import java.util.List;

@Dao
public interface ConversationDAO {

    @Query("SELECT * FROM Conversation ORDER BY createdAt DESC")
    List<Conversation> getAll();

    @Query("SELECT * FROM Conversation WHERE petId = :petId ORDER BY createdAt DESC")
    List<Conversation> getConversations(long petId);

    @Query("SELECT * FROM Conversation WHERE petId = :petId ORDER BY createdAt DESC LIMIT 1")
    Conversation getLastConversation(long petId);

    @Query("SELECT * FROM Conversation WHERE conversationId = :conversationId LIMIT 1")
    Conversation getConversationById(long conversationId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Conversation> conversations);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Conversation... conversations);

    @Delete()
    void delete(Conversation conversation);

    @Query("DELETE FROM Conversation WHERE petId = :petId")
    void deleteAll(long petId);
}
