package com.example.beagle.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.beagle.model.Pet;

import java.util.List;

@Dao
public interface PetDAO {

    @Query("SELECT * FROM Pet")
    List<Pet> getAll();

    @Query("SELECT * FROM Pet WHERE petId IN (:petIds)")
    List<Pet> loadAllByIds(long[] petIds);

    @Query("SELECT * FROM Pet WHERE petId = :petId LIMIT 1")
    Pet getPetById(long petId);

    @Insert
    void insert(Pet... pets);

    @Insert
    void insertAll(List<Pet> petList);

    @Delete
    void delete(Pet pet);

}
