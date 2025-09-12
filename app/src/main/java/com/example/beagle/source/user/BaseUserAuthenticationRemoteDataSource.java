package com.example.beagle.source.user;

import com.example.beagle.model.User;
import com.example.beagle.repository.user.UserResponseCallback;

/**
 * Base class to manage the user authentication (login/register + Google).
 */
public abstract class BaseUserAuthenticationRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }

    /** Utente attualmente loggato (se presente). */
    public abstract User getLoggedUser();

    /** Effettua il logout. */
    public abstract void logout();

    /** Registrazione con email/password. */
    public abstract void signUp(String email, String password);

    /** Login con email/password. */
    public abstract void signIn(String email, String password);

    /** Login con Google One Tap (idToken). */
    public abstract void signInWithGoogle(String idToken);
}
