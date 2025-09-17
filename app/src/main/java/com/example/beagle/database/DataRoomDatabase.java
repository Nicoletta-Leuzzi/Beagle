package com.example.beagle.database;

import static com.example.beagle.util.Constants.DATABASE_VERSION;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.beagle.model.Conversation;
import com.example.beagle.model.Message;
import com.example.beagle.model.Pet;
import com.example.beagle.model.User;
import com.example.beagle.util.Constants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Message.class, Conversation.class, Pet.class},
        version = DATABASE_VERSION, exportSchema = true)
public abstract class DataRoomDatabase extends RoomDatabase {

    public abstract MessageDAO messageDao();
    public abstract ConversationDAO conversationDao();
    public abstract PetDAO petDao();

    private static volatile DataRoomDatabase INSTANCE;

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static DataRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DataRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DataRoomDatabase.class, Constants.SAVED_DATABASE)
                            .allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }

}
