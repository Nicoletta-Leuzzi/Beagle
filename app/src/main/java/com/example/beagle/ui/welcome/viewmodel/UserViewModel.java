package com.example.beagle.ui.welcome.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
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

    // 🔄 NIENTE più LiveData cache condivisa per login/signup/logout
    // private MutableLiveData<Result> userMutableLiveData;

    // risultato del reset password
    private final MutableLiveData<Result> resetResult = new MutableLiveData<>();

    // risultato invio email di verifica
    private final MutableLiveData<Result> verificationResult = new MutableLiveData<>();

    // LiveData STABILI per osservazione "una volta sola" dal Fragment (se usi le azioni)
    private final MutableLiveData<Result> emailLoginResult  = new MutableLiveData<>();
    private final MutableLiveData<Result> googleLoginResult = new MutableLiveData<>();

    private boolean authenticationError = false;

    /** Costruttore di default (comodo per test/prototipi). */
    public UserViewModel() {
        this(new UserRepository());
    }

    /** Costruttore per DI (usato dalla UserViewModelFactory). */
    public UserViewModel(@NonNull IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ===================== API "vecchio stile" (compatibilità) =====================

    /** Entry point stile prof: se isUserRegistered==true -> login, altrimenti -> signup.
     *  RITORNA SEMPRE una nuova LiveData dal repository (niente cache). */
    public LiveData<Result> getUserMutableLiveData(String email, String password, boolean isUserRegistered) {
        return userRepository.getUser(email, password, isUserRegistered);
    }

    /** Variante per Google One Tap: prende l'idToken e delega al repo.
     *  Anche qui: SEMPRE una nuova LiveData. */
    public LiveData<Result> getGoogleUserMutableLiveData(String token) {
        return userRepository.getGoogleUser(token);
    }

    /** Chiamata “fire and forget” (lasciata per compatibilità). */
    public void getUser(String email, String password, boolean isUserRegistered) {
        userRepository.getUser(email, password, isUserRegistered);
    }

    /** Comode scorciatoie (ritornano una NUOVA LiveData ogni volta). */
    public LiveData<Result> login(String email, String password) {
        return getUserMutableLiveData(email, password, true);
    }

    public LiveData<Result> register(String email, String password) {
        return getUserMutableLiveData(email, password, false);
    }

    /** Logout: restituisce direttamente la LiveData del repo (niente cache). */
    public LiveData<Result> logout() {
        return userRepository.logout();
    }

    // ===================== API NUOVE per il LoginFragment (opzionali) =====================

    /** Getter stabili osservati UNA SOLA VOLTA nel LoginFragment. */
    public LiveData<Result> getEmailLoginResult() {
        return emailLoginResult;
    }

    public LiveData<Result> getGoogleLoginResult() {
        return googleLoginResult;
    }

    /**
     * Azione: login email/password.
     * Osserva one-shot la sorgente del repo e riversa l'esito su emailLoginResult.
     */
    public void loginWithEmail(String email, String password) {
        LiveData<Result> src = userRepository.getUser(email, password, /*isUserRegistered=*/true);
        Observer<Result> once = new Observer<Result>() {
            @Override public void onChanged(Result r) {
                emailLoginResult.setValue(r);
                src.removeObserver(this); // evita leak: osservazione one-shot
            }
        };
        src.observeForever(once);
    }

    /**
     * Azione: login tramite Google (idToken).
     */
    public void loginWithGoogle(String idToken) {
        LiveData<Result> src = userRepository.getGoogleUser(idToken);
        Observer<Result> once = new Observer<Result>() {
            @Override public void onChanged(Result r) {
                googleLoginResult.setValue(r);
                src.removeObserver(this);
            }
        };
        src.observeForever(once);
    }

    // ===================== Stato utente / helper =====================

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

    // ===================== RESET PASSWORD =====================

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


}
