package com.example.beagle.source.pet;

import com.example.beagle.model.Pet;
import com.example.beagle.repository.pet.IPetResponseCallback;

public abstract class BasePetRemoteDataSource {

    protected IPetResponseCallback petCallback;

    public void setPetCallback(IPetResponseCallback petCallback) {
        this.petCallback = petCallback;
    }

    public abstract void getPets();

    public abstract void insertPet(Pet pet);
}
