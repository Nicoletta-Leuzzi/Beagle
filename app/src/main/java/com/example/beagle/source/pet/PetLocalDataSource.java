package com.example.beagle.source.pet;

import com.example.beagle.database.DataRoomDatabase;
import com.example.beagle.database.PetDAO;
import com.example.beagle.model.Pet;

import java.util.List;

public class PetLocalDataSource extends BasePetLocalDataSource {

    private final PetDAO petDAO;

    public PetLocalDataSource(DataRoomDatabase dataRoomDatabase) {
        this.petDAO = dataRoomDatabase.petDao();
    }

    @Override
    public void getPets() {
        DataRoomDatabase.databaseWriteExecutor.execute(() -> {
            petCallback.onSuccessFromLocal(petDAO.getAll());
        });
    }

    @Override
    public void insertPet(Pet pet) {
        DataRoomDatabase.databaseWriteExecutor.execute(() -> {
            petDAO.insert(pet);
            petCallback.onSuccessFromLocal(petDAO.getAll());
        });
    }

    @Override
    public void insertPets(List<Pet> petList) {
        DataRoomDatabase.databaseWriteExecutor.execute(() -> {
            petDAO.insertAll(petList);
            petCallback.onSuccessFromLocal(petDAO.getAll());
        });
    }

    @Override
    public void deletePet(Pet pet) {
        DataRoomDatabase.databaseWriteExecutor.execute(() -> {
            petDAO.delete(pet);
            List<Pet> newPetList = petDAO.getAll();
            petCallback.onSuccessFromLocal(newPetList);
        });
    }
}
