package com.example.beagle.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.beagle.model.Message;

import java.util.List;

@Dao
public interface MessageDAO {

    @Query("SELECT * FROM Message ORDER BY seq ASC")
    List<Message> getAll();

    @Query("SELECT * FROM Message WHERE conversationId = :conversationId ORDER BY seq ASC")
    List<Message> getMessages(long conversationId);

    @Query("SELECT * FROM Message WHERE conversationId = :conversationId AND seq = :seq")
    Message getSingleMessage(long conversationId, int seq);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Message> messagesList);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Message... messages);

    @Update
    void updateMessages(List<Message> messages);

    @Delete
    void delete(Message message);

}
