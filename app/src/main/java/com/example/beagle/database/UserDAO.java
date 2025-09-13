package com.example.beagle.database;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.beagle.model.User;

@Dao
public interface UserDAO {

    @Query("SELECT * FROM User WHERE email = :email")
    User getUserByEmail(String email);
}
