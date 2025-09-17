package com.example.beagle.ui.chat.viewmodel.pet;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.beagle.model.Result;
import com.example.beagle.repository.pet.PetRepository;

public class PetReadViewModel extends ViewModel {
    private final PetRepository petRepository;
    private MutableLiveData<Result> petsListLiveData;

    public PetReadViewModel(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public MutableLiveData<Result> getPets(boolean fromRemote) {
        petsListLiveData = petRepository.fetchPets(fromRemote);
        return petsListLiveData;
    }
}
