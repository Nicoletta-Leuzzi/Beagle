package com.example.beagle.source.pet;

import com.example.beagle.model.Conversation;
import com.example.beagle.model.Pet;
import com.example.beagle.repository.pet.IPetResponseCallback;

import java.util.List;

public abstract class BasePetLocalDataSource {

    protected IPetResponseCallback petCallback;

    public void setPetCallback(IPetResponseCallback petCallback) {
        this.petCallback = petCallback;
    }

    public abstract void getPets();
    public abstract void insertPets(List<Pet> petList);
    public abstract void insertPet(Pet pet);
    public abstract void deletePet(Pet pet);
}
