package com.example.beagle.ui.chat.viewmodel.pet;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.beagle.model.Pet;
import com.example.beagle.model.Result;
import com.example.beagle.repository.pet.PetRepository;

public class PetWriteViewModel extends ViewModel {

    private final PetRepository petRepository;
    private MutableLiveData<Result> petSavedLiveData;
    private MutableLiveData<Result> petDeletedLiveData;

    public PetWriteViewModel(PetRepository petRepository) {
        this.petRepository = petRepository;
    }



    public MutableLiveData<Result> addPet(Pet pet) {
        petSavedLiveData = petRepository.addPet(pet);
        return petSavedLiveData;
    }

    public MutableLiveData<Result> deletePet(Pet pet) {
        petDeletedLiveData = petRepository.deletePet(pet);
        return petDeletedLiveData;
    }
}
