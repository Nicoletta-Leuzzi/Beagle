package com.example.beagle.ui.chat.viewmodel.pet;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.beagle.repository.pet.PetRepository;

public class PetWriteViewModelFactory implements ViewModelProvider.Factory {

    private final PetRepository petRepository;

    public PetWriteViewModelFactory(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new PetWriteViewModel(petRepository);
    }
}
