package com.example.beagle.database;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.beagle.model.Pet;

import java.util.List;

@Dao
public interface PetDAO {

    @Query("SELECT * FROM Pet")
    List<Pet> getAll();
}
