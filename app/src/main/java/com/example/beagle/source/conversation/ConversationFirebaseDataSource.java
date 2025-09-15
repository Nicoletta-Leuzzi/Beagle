package com.example.beagle.source.conversation;

import static com.example.beagle.util.Constants.FIREBASE_CONVERSATION_COLLECTION;
import static com.example.beagle.util.Constants.FIREBASE_REALTIME_DATABASE;

import com.example.beagle.model.Conversation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConversationFirebaseDataSource extends BaseConversationRemoteDataSource{

    private final DatabaseReference databaseReference;


    public ConversationFirebaseDataSource() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        this.databaseReference = firebaseDatabase.getReference().getRef();
    }

    @Override
    public void getConversations(long petId) {
        // TODO
    }

    @Override
    public void insertConversation(Conversation conversation, long petId) {
        databaseReference.child(FIREBASE_CONVERSATION_COLLECTION).
                child(Long.toString(petId)).child(Long.toString(conversation.getConversationId())).
                setValue(conversation);
    }
}
