package com.example.beagle.repository.user;

import com.example.beagle.model.User;

public interface UserResponseCallback {
    void onSuccess(User user);
    void onError(Exception e);
}
