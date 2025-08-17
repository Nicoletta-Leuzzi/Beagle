package com.example.beagle.source.user;

import com.example.beagle.model.User;
import com.example.beagle.repository.user.UserResponseCallback;

public class UserAuthenticationStubDataSource implements BaseUserAuthenticationRemoteDataSource {
    private boolean logged = false;

    @Override
    public void register(String name, String email, String password, UserResponseCallback cb) {
        logged = true;
        cb.onSuccess(new User(name, email, "stub-token"));
    }

    @Override
    public void login(String email, String password, UserResponseCallback cb) {
        logged = true;
        // non conosciamo il nome negli stub → null/placeholder
        cb.onSuccess(new User(null, email, "stub-token"));
    }

    @Override public void logout() { logged = false; }
    @Override public boolean isLoggedIn() { return logged; }
}
