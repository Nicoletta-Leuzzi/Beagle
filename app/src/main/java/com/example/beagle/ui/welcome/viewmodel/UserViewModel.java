package com.example.beagle.ui.welcome.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.beagle.model.Result;
import com.example.beagle.model.User;
import com.example.beagle.repository.user.IUserRepository;
import com.example.beagle.repository.user.UserRepository;

//ViewModel per l'autenticazione
public class UserViewModel extends ViewModel {

    private final IUserRepository userRepository;

    // risultato del reset password
    private final MutableLiveData<Result> resetResult = new MutableLiveData<>();



    // LiveData STABILI per osservazione "una volta sola" dal Fragment
    private final MutableLiveData<Result> emailLoginResult  = new MutableLiveData<>();
    private final MutableLiveData<Result> googleLoginResult = new MutableLiveData<>();

    private boolean authenticationError = false;

    //Costruttore di default
    public UserViewModel() {
        this(new UserRepository());
    }

    /** Costruttore per DI (usato dalla UserViewModelFactory). */
    public UserViewModel(@NonNull IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** se isUserRegistered==true -> login, altrimenti -> signup.
     *  RITORNA SEMPRE una nuova LiveData dal repository (niente cache). */
    public LiveData<Result> getUserMutableLiveData(String email, String password, boolean isUserRegistered) {
        return userRepository.getUser(email, password, isUserRegistered);
    }

    /** Variante per Google One Tap: prende l'idToken e delega al repo.*/
    public LiveData<Result> getGoogleUserMutableLiveData(String token) {
        return userRepository.getGoogleUser(token);
    }


    //Stato utente / helper

    /** Utente attualmente loggato (se presente nel provider sottostante). */
    public User getLoggedUser() {
        return userRepository.getLoggedUser();
    }



    public void setAuthenticationError(boolean authenticationError) {
        this.authenticationError = authenticationError;
    }

    //RESET PASSWORD

    public LiveData<Result> resetPassword(String email) {
        userRepository.sendPasswordReset(email)
                .addOnSuccessListener(v -> resetResult.postValue(new Result.UserSuccess(null)))
                .addOnFailureListener(e -> resetResult.postValue(
                        new Result.Error(e.getMessage() != null
                                ? e.getMessage()
                                : "Si è verificato un errore. Riprova.")
                ));
        return resetResult;
    }


}
