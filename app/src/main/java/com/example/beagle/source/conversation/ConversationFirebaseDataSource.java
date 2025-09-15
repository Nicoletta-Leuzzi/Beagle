package com.example.beagle.source.conversation;

import static com.example.beagle.util.Constants.FIREBASE_CONVERSATION_COLLECTION;
import static com.example.beagle.util.Constants.FIREBASE_REALTIME_DATABASE;
import static com.example.beagle.util.Constants.FIREBASE_USERS_COLLECTION;

import com.example.beagle.model.Conversation;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConversationFirebaseDataSource extends BaseConversationRemoteDataSource{

    private final DatabaseReference databaseReference;
    private final String user;


    public ConversationFirebaseDataSource() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.databaseReference = firebaseDatabase.getReference().getRef()
                .child(FIREBASE_USERS_COLLECTION)
                .child(user)
                .child(FIREBASE_CONVERSATION_COLLECTION);
    }

    @Override
    public void getConversations(long petId) {
        // TODO
    }

    @Override
    public void insertConversation(Conversation conversation, long petId) {
        databaseReference.
                child(Long.toString(petId)).child(Long.toString(conversation.getConversationId())).
                setValue(conversation);
    }
}
