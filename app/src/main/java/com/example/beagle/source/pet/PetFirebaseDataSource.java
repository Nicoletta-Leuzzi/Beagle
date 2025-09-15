package com.example.beagle.source.pet;

import static com.example.beagle.util.Constants.FIREBASE_CONVERSATION_COLLECTION;
import static com.example.beagle.util.Constants.FIREBASE_PET_COLLECTION;
import static com.example.beagle.util.Constants.FIREBASE_REALTIME_DATABASE;
import static com.example.beagle.util.Constants.FIREBASE_USERS_COLLECTION;

import com.example.beagle.model.Pet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PetFirebaseDataSource extends BasePetRemoteDataSource {

    public final DatabaseReference databaseReference;
    private final String user;

    public PetFirebaseDataSource() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.databaseReference = firebaseDatabase.getReference().getRef()
                .child(FIREBASE_USERS_COLLECTION)
                .child(user)
                .child(FIREBASE_PET_COLLECTION);
    }

    @Override
    public void getPets() {
        // TODO
    }

    @Override
    public void insertPet(Pet pet) {
        databaseReference.child(Long.toString(pet.getPetId()))
                .setValue(pet);
    }
}
