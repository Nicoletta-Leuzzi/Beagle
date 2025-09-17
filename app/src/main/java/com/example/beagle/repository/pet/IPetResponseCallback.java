package com.example.beagle.repository.pet;

import com.example.beagle.model.Pet;

import java.util.List;

public interface IPetResponseCallback {

    void onSuccessFromRemote();
    void onSuccessFromLocal(List<Pet> petList);
    void onSuccessReadFromRemote(List<Pet> petList);
    void onSuccessReadFromLocal(Pet pet);
    void onSuccessDeleteFromRemote(long petId);
    void onSuccessDeleteFromLocal(List<Pet> petList);


    void onFailureFromRemote(Exception exception);
    void onFailureReadFromRemote(Exception exception);
    void onFailureFromLocal(Exception exception);
    void onFailureDeleteFromRemote(Exception exception);
    void onFailureDeleteFromLocal(Exception exception);

}