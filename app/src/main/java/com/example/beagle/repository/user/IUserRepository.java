package com.example.beagle.repository.user;

import androidx.lifecycle.MutableLiveData;

import com.example.beagle.model.Result;
import com.example.beagle.model.User;

/** Interfaccia repo utente – allineata allo stile del prof, solo auth. */
public interface IUserRepository {

    /** Entry point unificato: se isUserRegistered=true → login, altrimenti → signup. */
    MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered);

    /** Variante per Google One Tap / Sign-In con idToken. */
    MutableLiveData<Result> getGoogleUser(String idToken);

    /** Logout: usa lo stesso LiveData per notificare la UI. */
    MutableLiveData<Result> logout();

    /** Utente attualmente loggato (se disponibile). */
    User getLoggedUser();

    /** Operazioni “dirette” come nel prof (usate internamente dal repo). */
    void signUp(String email, String password);
    void signIn(String email, String password);
    void signInWithGoogle(String token);
}
