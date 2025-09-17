package com.example.beagle.source.conversation;

import static com.example.beagle.util.Constants.FIREBASE_CONVERSATION_COLLECTION;
import static com.example.beagle.util.Constants.FIREBASE_MESSAGES_COLLECTION;
import static com.example.beagle.util.Constants.FIREBASE_REALTIME_DATABASE;
import static com.example.beagle.util.Constants.FIREBASE_USERS_COLLECTION;

import com.example.beagle.model.Conversation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConversationFirebaseDataSource extends BaseConversationRemoteDataSource {

    private final DatabaseReference databaseReference;
    private final DatabaseReference messagesReference;
    private String uid;


    public ConversationFirebaseDataSource() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        DatabaseReference userRoot = firebaseDatabase.getReference().getRef()
                .child(FIREBASE_USERS_COLLECTION)
                .child(uid);

        this.databaseReference = userRoot
                .child(FIREBASE_CONVERSATION_COLLECTION);

        this.messagesReference = userRoot
                .child(FIREBASE_MESSAGES_COLLECTION);
    }


    @Override
    public void getConversations(long petId) {

        databaseReference.child(String.valueOf(petId))
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        conversationCallback.onFailureReadFromRemote(task.getException());
                        return;
                    }
                    List<Conversation> list = new ArrayList<>();
                    for (DataSnapshot ds : task.getResult().getChildren()) {
                        Conversation c = ds.getValue(Conversation.class);
                        if (c != null) list.add(c);
                    }
                    conversationCallback.onSuccessReadFromRemote(list, petId);
                });
    }

    @Override
    public void insertConversation(Conversation conversation, long petId) {
        databaseReference.
                child(Long.toString(petId))
                .child(Long.toString(conversation.getConversationId()))
                .setValue(conversation);
    }

    @Override
    public void deleteConversation(long conversationId, long petId) {
        Task<Void> delConversation = databaseReference
                .child(String.valueOf(petId))
                .child(String.valueOf(conversationId))
                .removeValue();

        Task<Void> delMessages = messagesReference
                .child(String.valueOf(conversationId))
                .removeValue();

        Tasks.whenAll(delConversation, delMessages)
                .addOnSuccessListener(task -> {
                    boolean ok = delConversation.isSuccessful() && delMessages.isSuccessful();
                    if (ok) {
                        conversationCallback.onSuccessDeleteFromRemote(conversationId, petId);
                    }
                })
                .addOnFailureListener(exception -> {
                    boolean ok = delConversation.isSuccessful() && delMessages.isSuccessful();
                    if (!ok) {
                        conversationCallback.onFailureDeleteFromRemote(conversationId, petId, exception);
                    }
                });
    }
}
