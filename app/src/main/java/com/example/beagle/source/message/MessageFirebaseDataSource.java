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
        databaseReference = firebaseDatabase.getReference("messages").getRef();
    }



    @Override
    public void getMessages(long conversationId) {
        // NON FUNGE
        /*
        Log.d("Test", "FIREBASE GET MESSAGES");
        databaseReference.child(Long.toString(conversationId)).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                messageCallback.onFailureFromRemote(task.getException().getLocalizedMessage());
            } else {
                List<Message> messageList = new ArrayList<>();
                for(DataSnapshot ds : task.getResult().getChildren()) {
                    Message message = ds.getValue(Message.class);
                    Log.d(TAG, "INSIDE FOR CICLE" + message.getMessageContent());
                    messageList.add(message);
                }

                messageCallback.onSuccessFromRemote(messageList);
            }
        });

         */


    }

    @Override
    public void insertMessage(Message message) {
        databaseReference.child(Long.toString(message.getConversationId())).child(Long.toString(message.getSeq())).setValue(message);
    }
}
