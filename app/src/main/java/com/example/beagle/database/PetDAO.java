package com.example.beagle.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.beagle.model.Pet;

import java.util.List;

@Dao
public interface PetDAO {

    @Query("SELECT * FROM Pet")
    List<Pet> getAll();

    @Query("SELECT * FROM Pet WHERE petId IN (:petIds)")
    List<Pet> loadAllByIds(long[] petIds);

    @Query("SELECT * FROM Pet WHERE name LIKE :name LIMIT 1")
    Pet findByName(String name);

    @Insert
    void insertAll(Pet... pets);

    @Delete
    void delete(Pet pet);
}
