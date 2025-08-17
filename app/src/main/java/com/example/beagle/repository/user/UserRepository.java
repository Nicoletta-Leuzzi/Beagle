package com.example.beagle.repository.user;

import androidx.annotation.Nullable;

import com.example.beagle.model.User;
import com.example.beagle.source.user.BaseUserAuthenticationRemoteDataSource;
import com.example.beagle.source.user.BaseUserDataRemoteDataSource;
import com.example.beagle.source.user.UserAuthenticationStubDataSource;
import com.example.beagle.source.user.UserDataStubDataSource;

public class UserRepository implements IUserRepository {

    private final BaseUserAuthenticationRemoteDataSource authDs;
    private final BaseUserDataRemoteDataSource dataDs;

    // Versione STUB per build verde (no AuthStore, no Firebase)
    public UserRepository() {
        this.authDs = new UserAuthenticationStubDataSource();
        this.dataDs = new UserDataStubDataSource();
    }

    @Override
    public void register(String name, String email, String password, UserResponseCallback cb) {
        // Lo "name" lo ignoriamo negli stub: passiamo l'oggetto restituito dall'auth
        authDs.register(name, email, password, new UserResponseCallback() {
            @Override
            public void onSuccess(User user) {
                dataDs.saveUser("stub-uid", user, new UserResponseCallback() {
                    @Override public void onSuccess(User saved) { cb.onSuccess(saved); }
                    @Override public void onError(Exception e) { cb.onError(e); }
                });
            }
            @Override public void onError(Exception e) { cb.onError(e); }
        });
    }

    @Override
    public void login(String email, String password, UserResponseCallback cb) {
        authDs.login(email, password, new UserResponseCallback() {
            @Override
            public void onSuccess(User userFromAuth) {
                // Negli stub recuperiamo eventualmente un profilo salvato ma, in ogni caso,
                // ritorniamo l'utente dell'auth (ha già email + idToken).
                dataDs.getUser("stub-uid", new UserResponseCallback() {
                    @Override public void onSuccess(@Nullable User dbUser) {
                        cb.onSuccess(userFromAuth);
                    }
                    @Override public void onError(Exception e) {
                        cb.onSuccess(userFromAuth); // fallback
                    }
                });
            }
            @Override public void onError(Exception e) { cb.onError(e); }
        });
    }

    @Override
    public void logout() {
        authDs.logout();
    }

    @Override
    public User getCurrentUserOrNull() {
        // Negli stub non persistiamo sessione
        return null;
    }
}
