package com.example.beagle.ui.welcome.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.beagle.model.Result;
import com.example.beagle.model.User;
import com.example.beagle.repository.user.IUserRepository;
import com.example.beagle.repository.user.UserRepository;

/**
 * ViewModel per l'autenticazione (login/registrazione).
 */
public class UserViewModel extends ViewModel {

    private final IUserRepository userRepository;
    private MutableLiveData<Result> userMutableLiveData;

    // risultato del reset password
    private final MutableLiveData<Result> resetResult = new MutableLiveData<>();

    // risultato invio email di verifica
    private final MutableLiveData<Result> verificationResult = new MutableLiveData<>();

    private boolean authenticationError = false;

    /** Costruttore di default (comodo per test/prototipi). */
    public UserViewModel() {
        this(new UserRepository());
    }

    /** Costruttore per DI (usato dalla UserViewModelFactory). */
    public UserViewModel(@NonNull IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Entry point: se isUserRegistered==true -> login, altrimenti -> signup. */
    public LiveData<Result> getUserMutableLiveData(String email, String password, boolean isUserRegistered) {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.getUser(email, password, isUserRegistered);
        }
        return userMutableLiveData;
    }

    /** Variante per Google One Tap: prende l'idToken e delega al repo. */
    public LiveData<Result> getGoogleUserMutableLiveData(String token) {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.getGoogleUser(token);
        }
        return userMutableLiveData;
    }

    /** Chiamata “fire and forget” */
    public void getUser(String email, String password, boolean isUserRegistered) {
        userRepository.getUser(email, password, isUserRegistered);
    }

    /** Comode scorciatoie. */
    public LiveData<Result> login(String email, String password) {
        return getUserMutableLiveData(email, password, true);
    }

    public LiveData<Result> register(String email, String password) {
        return getUserMutableLiveData(email, password, false);
    }

    /** Logout: riusa lo stesso LiveData per notificare la UI. */
    public LiveData<Result> logout() {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.logout();
        } else {
            userRepository.logout();
        }
        return userMutableLiveData;
    }

    /** Utente attualmente loggato (se presente nel provider sottostante). */
    public User getLoggedUser() {
        return userRepository.getLoggedUser();
    }

    /** Flag d’errore di autenticazione. */
    public boolean isAuthenticationError() {
        return authenticationError;
    }

    public void setAuthenticationError(boolean authenticationError) {
        this.authenticationError = authenticationError;
    }

    // ========= RESET PASSWORD =========
    public LiveData<Result> resetPassword(String email) {
        userRepository.sendPasswordReset(email)
                .addOnSuccessListener(v -> resetResult.postValue(new Result.UserSuccess(null)))
                .addOnFailureListener(e -> resetResult.postValue(
                        new Result.Error(e != null && e.getMessage() != null
                                ? e.getMessage()
                                : "Si è verificato un errore. Riprova.")
                ));
        return resetResult;
    }

    // ========= EMAIL VERIFICATION =========
    /** Invia (o reinvia) l'email di verifica all'utente attualmente autenticato. */
    public LiveData<Result> resendEmailVerification() {
        userRepository.sendEmailVerification()
                .addOnSuccessListener(v -> verificationResult.postValue(new Result.UserSuccess(null)))
                .addOnFailureListener(e -> verificationResult.postValue(
                        new Result.Error(e != null && e.getMessage() != null
                                ? e.getMessage()
                                : "Errore invio email di verifica")
                ));
        return verificationResult;
    }
}
