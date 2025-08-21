package com.example.beagle.ui.welcome.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.beagle.model.User;
import com.example.beagle.repository.user.IUserRepository;
import com.example.beagle.repository.user.UserResponseCallback;

public class UserViewModel extends ViewModel {

    private final IUserRepository repo;

    private final MutableLiveData<AuthState> state = new MutableLiveData<>(AuthState.IDLE);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);
    private final MutableLiveData<User> currentUser = new MutableLiveData<>(null);

    public UserViewModel(@NonNull IUserRepository repo) {
        this.repo = repo;
        // Autologin se disponibile (negli stub torna null)
        User u = repo.getCurrentUserOrNull();
        if (u != null) currentUser.postValue(u);
    }

    public LiveData<AuthState> getState() { return state; }
    public LiveData<String> getError() { return error; }
    public LiveData<User> getCurrentUser() { return currentUser; }

    public void login(String email, String password) {
        error.postValue(null);
        state.postValue(AuthState.LOADING);
        repo.login(email, password, new UserResponseCallback() {
            @Override public void onSuccess(User user) {
                currentUser.postValue(user);
                state.postValue(AuthState.SUCCESS);
            }
            @Override public void onError(Exception e) {
                error.postValue(safeMsg(e));
                state.postValue(AuthState.IDLE);
            }
        });
    }

    public void register(String name, String email, String password) {
        error.postValue(null);
        state.postValue(AuthState.LOADING);
        repo.register(name, email, password, new UserResponseCallback() {
            @Override public void onSuccess(User user) {
                currentUser.postValue(user);
                state.postValue(AuthState.SUCCESS);
            }
            @Override public void onError(Exception e) {
                error.postValue(safeMsg(e));
                state.postValue(AuthState.IDLE);
            }
        });
    }

    public void logout() {
        try { repo.logout(); } catch (Exception ignored) {}
        currentUser.postValue(null);
        error.postValue(null);
        state.postValue(AuthState.IDLE);
    }

    private String safeMsg(Exception e) {
        String m = e.getMessage();
        return (m == null || m.trim().isEmpty()) ? e.getClass().getSimpleName() : m;
    }
}
