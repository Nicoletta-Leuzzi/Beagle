package com.example.beagle.source.pet;

import static com.example.beagle.util.Constants.FIREBASE_PET_COLLECTION;
import static com.example.beagle.util.Constants.FIREBASE_REALTIME_DATABASE;

import com.example.beagle.model.Pet;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PetFirebaseDataSource extends BasePetRemoteDataSource {

    public final DatabaseReference databaseReference;

    public PetFirebaseDataSource() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        this.databaseReference = firebaseDatabase.getReference().getRef();
    }

    @Override
    public void getPets() {
        // TODO
    }

    @Override
    public void insertPet(Pet pet) {
        databaseReference.child(FIREBASE_PET_COLLECTION).child(Long.toString(pet.getPetId()))
                .setValue(pet);
    }
}
