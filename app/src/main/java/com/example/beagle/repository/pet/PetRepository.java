package com.example.beagle.repository.pet;

import androidx.lifecycle.MutableLiveData;

import com.example.beagle.model.Pet;
import com.example.beagle.model.Result;
import com.example.beagle.source.pet.BasePetLocalDataSource;
import com.example.beagle.source.pet.BasePetRemoteDataSource;

import java.util.List;

public class PetRepository implements IPetResponseCallback {

    private final MutableLiveData<Result> petMutableLiveData;
    private final BasePetRemoteDataSource petRemoteDataSource;
    private final BasePetLocalDataSource petLocalDataSource;

    public PetRepository(BasePetRemoteDataSource petRemoteDataSource,
                         BasePetLocalDataSource petLocalDataSource) {
        petMutableLiveData = new MutableLiveData<>();
        this.petRemoteDataSource = petRemoteDataSource;
        this.petLocalDataSource = petLocalDataSource;
        this.petRemoteDataSource.setPetCallback(this);
        this.petLocalDataSource.setPetCallback(this);
    }


    public MutableLiveData<Result> fetchPets(boolean fromRemote) {
        if (fromRemote) {
            petRemoteDataSource.getPets();
        } else {
            petLocalDataSource.getPets();
        }

        return petMutableLiveData;
    }

    public void addPet(Pet pet) {
        petLocalDataSource.insertPet(pet);
    }

    @Override
    public void onSuccessFromRemote() {
        // TODO
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        petMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromLocal(List<Pet> petList) {
        Result.PetSuccess result = new Result.PetSuccess(petList);
        petMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureFromLocal(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        petMutableLiveData.postValue(result);
    }
}
