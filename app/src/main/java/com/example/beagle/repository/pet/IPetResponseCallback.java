package com.example.beagle.repository.pet;

import com.example.beagle.model.Pet;

import java.util.List;

public interface IPetResponseCallback {

    void onSuccessFromRemote();
    void onSuccessFromLocal(List<Pet> petList);
    void onSuccessReadFromLocal(Pet pet);

    void onFailureFromRemote(Exception exception);
    void onFailureFromLocal(Exception exception);

    void onSuccessDeleteFromLocal(List<Pet> petList);
}
