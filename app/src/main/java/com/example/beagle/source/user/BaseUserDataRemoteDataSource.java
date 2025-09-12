package com.example.beagle.source.user;


import com.example.beagle.model.User;
import com.example.beagle.repository.user.UserResponseCallback;

import java.util.Set;

public abstract class BaseUserDataRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }

    public abstract void saveUserData(User user);
}
