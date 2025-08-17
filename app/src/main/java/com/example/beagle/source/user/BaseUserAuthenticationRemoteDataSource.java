package com.example.beagle.source.user;

import com.example.beagle.repository.user.UserResponseCallback;

public interface BaseUserAuthenticationRemoteDataSource {
    void register(String name, String email, String password, UserResponseCallback cb);
    void login(String email, String password, UserResponseCallback cb);
    void logout();
    boolean isLoggedIn();
}
