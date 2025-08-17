package com.example.beagle.source.user;

import com.example.beagle.model.User;
import com.example.beagle.repository.user.UserResponseCallback;

public interface BaseUserDataRemoteDataSource {
    void saveUser(String uid, User user, UserResponseCallback cb);
    void getUser(String uid, UserResponseCallback cb);
}
