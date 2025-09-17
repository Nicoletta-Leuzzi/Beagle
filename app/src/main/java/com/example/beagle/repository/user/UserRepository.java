package com.example.beagle.repository.user;

import androidx.lifecycle.MutableLiveData;

import com.example.beagle.model.Result;
import com.example.beagle.model.User;
import com.example.beagle.source.user.BaseUserAuthenticationRemoteDataSource;
import com.example.beagle.source.user.UserAuthenticationFirebaseDataSource;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class UserRepository implements IUserRepository {

    private final BaseUserAuthenticationRemoteDataSource userRemoteDataSource;
    private final MutableLiveData<Result> userMutableLiveData = new MutableLiveData<>();

    // Puoi passare l'impl che vuoi; di default Firebase
    public UserRepository() {
        this(new UserAuthenticationFirebaseDataSource());
    }

    public UserRepository(BaseUserAuthenticationRemoteDataSource authDs) {
        this.userRemoteDataSource = authDs;
        // ----- CALLBACK UNICO  -----
        UserResponseCallback callback = new UserResponseCallback() {
            @Override
            public void onSuccessFromAuthentication(User user) {
                userMutableLiveData.postValue(new Result.UserSuccess(user));
            }

            @Override
            public void onFailureFromAuthentication(String message) {
                userMutableLiveData.postValue(new Result.Error(message));
            }

            @Override
            public void onSuccessFromRemoteDatabase(User user) {
                userMutableLiveData.postValue(new Result.UserSuccess(user));
            }

            @Override
            public void onFailureFromRemoteDatabase(String message) {
                userMutableLiveData.postValue(new Result.Error(message));
            }

            @Override
            public void onSuccessLogout() {
                userMutableLiveData.postValue(new Result.UserSuccess(null));
            }
        };
        this.userRemoteDataSource.setUserResponseCallback(callback);
    }

    //FINE CALLBACK

    @Override
    public MutableLiveData<Result> getGoogleUser(String idToken) {
        signInWithGoogle(idToken);
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered) {
        if (isUserRegistered) {
            signIn(email, password);
        } else {
            signUp(email, password);
        }
        return userMutableLiveData;
    }

    @Override
    public void signIn(String email, String password) {
        userRemoteDataSource.signIn(email, password);
    }

    @Override
    public void signInWithGoogle(String token) {
        userRemoteDataSource.signInWithGoogle(token);
    }

    @Override
    public void signUp(String email, String password) {
        userRemoteDataSource.signUp(email, password);
    }

    @Override
    public MutableLiveData<Result> logout() {
        userRemoteDataSource.logout();
        return userMutableLiveData;
    }

    @Override
    public User getLoggedUser() {
        return userRemoteDataSource.getLoggedUser();
    }

    //Reset password
    @Override
    public Task<Void> sendPasswordReset(String email) {
        return FirebaseAuth.getInstance().sendPasswordResetEmail(email);
    }


}
