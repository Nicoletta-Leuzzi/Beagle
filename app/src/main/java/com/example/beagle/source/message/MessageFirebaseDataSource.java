package com.example.beagle.source.message;

import static com.example.beagle.util.Constants.FIREBASE_MESSAGES_COLLECTION;
import static com.example.beagle.util.Constants.FIREBASE_REALTIME_DATABASE;
import static com.example.beagle.util.Constants.FIREBASE_USERS_COLLECTION;

import android.util.Log;

import com.example.beagle.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MessageFirebaseDataSource extends BaseMessageRemoteDataSource {
    String TAG = "test";

    private final DatabaseReference databaseReference;
    private final String user;
    public MessageFirebaseDataSource() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = firebaseDatabase.getReference().getRef()
                .child(FIREBASE_USERS_COLLECTION)
                .child(user)
                .child(FIREBASE_MESSAGES_COLLECTION);
    }



    @Override
    public void getMessages(long conversationId) {

        databaseReference.child(Long.toString(conversationId)).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                messageCallback.onFailureReadFromRemote(task.getException());
            } else {
                List<Message> messageList = new ArrayList<>();
                for(DataSnapshot ds : task.getResult().getChildren()) {
                    Message message = ds.getValue(Message.class);
                    messageList.add(message);
                }

                messageCallback.onSuccessReadFromRemote(messageList, conversationId);
            }
        });




    }

    @Override
    public void insertMessage(Message message, long conversationId, int seq) {
        Log.d("test", "FIREBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAASE");
        databaseReference.child(Long.toString(conversationId)).child(Integer.toString(seq)).setValue(message);
    }
}
