package com.example.beagle.source.user;

import com.example.beagle.model.User;
import com.example.beagle.repository.user.UserResponseCallback;

public class UserDataStubDataSource implements BaseUserDataRemoteDataSource {
    private User cached;

    @Override
    public void saveUser(String uid, User user, UserResponseCallback cb) {
        cached = user;
        cb.onSuccess(user);
    }

    @Override
    public void getUser(String uid, UserResponseCallback cb) {
        cb.onSuccess(cached);
    }
}
