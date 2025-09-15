package com.example.beagle.source.message;

import static com.example.beagle.util.Constants.FIREBASE_REALTIME_DATABASE;

import android.util.Log;

import com.example.beagle.model.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MessageFirebaseDataSource extends BaseMessageRemoteDataSource {
    String TAG = "test";

    private final DatabaseReference databaseReference;
    public MessageFirebaseDataSource() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference("messages");
    }



    @Override
    public void getMessages(long conversationId) {

        Log.d("Firebase_remote", Long.toString(conversationId));

        Log.d("Firebase_remote", "FIREBASE GET MESSAGES");
        databaseReference.child(Long.toString(conversationId)).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                messageCallback.onFailureReadFromRemote(task.getException());
            } else {
                List<Message> messageList = new ArrayList<>();
                for(DataSnapshot ds : task.getResult().getChildren()) {
                    Message message = ds.getValue(Message.class);
                    //Log.d(TAG, "INSIDE FOR CICLE" + message.getContent());
                    messageList.add(message);
                    Log.d("Firebase_reote", "AAAAAAAAAAAA"+message.getContent());
                }
                Log.d("Firebase_reote", messageList+ "");

                messageCallback.onSuccessReadFromRemote(messageList, conversationId);
            }
        });




    }

    @Override
    public void insertMessage(Message message, long conversationId, long seq) {
        databaseReference.child(Long.toString(conversationId)).child(Long.toString(seq)).setValue(message);
    }
}
