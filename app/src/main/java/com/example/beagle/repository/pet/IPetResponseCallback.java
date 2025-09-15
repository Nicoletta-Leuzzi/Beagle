package com.example.beagle.repository.pet;

import com.example.beagle.model.Pet;

import java.util.List;

public interface IPetResponseCallback {
    void onSuccessFromRemote();
    void onFailureFromRemote(Exception exception);
    void onSuccessFromLocal(List<Pet> petList);
    void onFailureFromLocal(Exception exception);
}
