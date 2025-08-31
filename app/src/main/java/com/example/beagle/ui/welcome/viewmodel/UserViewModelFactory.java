package com.example.beagle.ui.welcome.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.beagle.repository.user.IUserRepository; // ✅ CORRETTO

public class UserViewModelFactory implements ViewModelProvider.Factory {
    private final IUserRepository repo;

    public UserViewModelFactory(@NonNull IUserRepository repo) {
        this.repo = repo;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(repo);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
