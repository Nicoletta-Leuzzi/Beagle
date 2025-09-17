package com.example.beagle.source.user;


import androidx.annotation.NonNull;

import com.example.beagle.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * Recupero/salvataggio dati utente su Firebase Realtime Database.
 * Versione senza gestione delle preferenze utente.
 */
public class UserFirebaseDataSource extends BaseUserDataRemoteDataSource {


    // Usa il DB di default del progetto (da google-services.json).
    private final DatabaseReference rootRef;

    // Nodi usati (rinomina se hai costanti dedicate)
    private static final String NODE_USERS = "users";

    public UserFirebaseDataSource() {
        rootRef = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Salva l'utente se non esiste già, altrimenti ritorna successo.
     */
    @Override
    public void saveUserData(User user) {
        if (user == null) {
            if (userResponseCallback != null) {
                userResponseCallback.onFailureFromRemoteDatabase("Invalid user");
            }
            return;
        }

        DatabaseReference userRef = rootRef.child(NODE_USERS).child(user.getIdToken());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Utente già presente
                    if (userResponseCallback != null) {
                        userResponseCallback.onSuccessFromRemoteDatabase(user);
                    }
                } else {
                    // Crea il nodo utente
                    userRef.setValue(user)
                            .addOnSuccessListener(unused -> {
                                if (userResponseCallback != null) {
                                    userResponseCallback.onSuccessFromRemoteDatabase(user);
                                }
                            })
                            .addOnFailureListener(e -> {
                                if (userResponseCallback != null) {
                                    userResponseCallback.onFailureFromRemoteDatabase(
                                            e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "Write failed");
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (userResponseCallback != null) {
                    userResponseCallback.onFailureFromRemoteDatabase(error.getMessage());
                }
            }
        });
    }
}



