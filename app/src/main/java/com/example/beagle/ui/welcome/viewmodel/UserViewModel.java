package com.example.beagle.ui.welcome.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<AuthState> state = new MutableLiveData<>(AuthState.IDLE);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);

    public LiveData<AuthState> getState() { return state; }
    public LiveData<String> getError() { return error; }

    // Stub per far compilare Login e Register
    public void login(String email, String password) {
        state.postValue(AuthState.SUCCESS);
    }

    public void register(String name, String email, String password) {
        state.postValue(AuthState.SUCCESS);
    }

    public void logout() {
        state.postValue(AuthState.IDLE);
    }
}
