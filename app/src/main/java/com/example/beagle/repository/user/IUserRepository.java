package com.example.beagle.repository.user;

import com.example.beagle.model.User;

public interface IUserRepository {
    void register(String name, String email, String password, UserResponseCallback cb);
    void login(String email, String password, UserResponseCallback cb);
    void logout();
    User getCurrentUserOrNull();
}
