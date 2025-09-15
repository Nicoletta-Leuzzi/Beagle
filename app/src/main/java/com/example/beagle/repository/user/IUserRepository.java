package com.example.beagle.repository.user;

import androidx.lifecycle.MutableLiveData;

import com.example.beagle.model.Result;
import com.example.beagle.model.User;
import com.google.android.gms.tasks.Task;

/** Interfaccia repo utente */
public interface IUserRepository {

    /** Entry point unificato: se isUserRegistered=true → login, altrimenti → signup. */
    MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered);

    /** Variante per Google One Tap / Sign-In con idToken. */
    MutableLiveData<Result> getGoogleUser(String idToken);

    /** Logout: usa lo stesso LiveData per notificare la UI. */
    MutableLiveData<Result> logout();

    /** Utente attualmente loggato (se disponibile). */
    User getLoggedUser();

    /** Operazioni “dirette”  */
    void signUp(String email, String password);
    void signIn(String email, String password);
    void signInWithGoogle(String token);

    /** Reset password via email */
    Task<Void> sendPasswordReset(String email);


}
