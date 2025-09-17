package com.example.beagle.source.pet;

import static com.example.beagle.util.Constants.FIREBASE_PET_COLLECTION;
import static com.example.beagle.util.Constants.FIREBASE_REALTIME_DATABASE;
import static com.example.beagle.util.Constants.FIREBASE_USERS_COLLECTION;

import com.example.beagle.model.Pet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PetFirebaseDataSource extends BasePetRemoteDataSource {

    public final DatabaseReference databaseReference;

    public PetFirebaseDataSource() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        String user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        this.databaseReference = firebaseDatabase.getReference().getRef()
                .child(FIREBASE_USERS_COLLECTION)
                .child(user)
                .child(FIREBASE_PET_COLLECTION);
    }

    @Override
    public void getPets() {
        databaseReference
                .get()
                .addOnCompleteListener(task -> {
                    if (petCallback == null) {
                        return;
                    }
                    if (!task.isSuccessful()) {
                        petCallback.onFailureReadFromRemote(task.getException());
                    } else {
                        List<Pet> petList = new ArrayList<>();
                        for (DataSnapshot ds : task.getResult().getChildren()) {
                            Pet pet = ds.getValue(Pet.class);
                            if (pet != null) {
                                petList.add(pet);
                            }
                        }

                        petCallback.onSuccessReadFromRemote(petList);
                    }
                });
    }

    @Override
    public void insertPet(Pet pet) {
        if (pet == null) {
            return;
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("petId", pet.getPetId());
        payload.put("name", pet.getName());
        payload.put("breed", pet.getBreed());
        payload.put("age", pet.getAge());
        payload.put("species", (int) pet.getSpecies());
        payload.put("birthDate", pet.getBirthDate());

        databaseReference
                .child(Long.toString(pet.getPetId()))
                .setValue(payload);
    }

    @Override
    public void deletePet(Pet pet) {
        databaseReference
                .child(Long.toString(pet.getPetId()))
                .removeValue()
                .addOnSuccessListener(unused -> {
                    if (petCallback != null) {
                        petCallback.onSuccessDeleteFromRemote(pet.getPetId());
                    }
                }).addOnFailureListener(exception -> {
                    if (petCallback != null) {
                        petCallback.onFailureDeleteFromRemote(exception);
                    }
                });
    }
}
