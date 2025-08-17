package com.example.beagle.util;

import android.content.Context;

import com.example.beagle.repository.user.IUserRepository;
import com.example.beagle.repository.user.UserRepository;

public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE;

    private IUserRepository userRepository;

    private ServiceLocator(Context appContext) {
        // Nessun AuthStore negli stub
    }

    public static ServiceLocator getInstance(Context appContext) {
        if (INSTANCE == null) {
            synchronized (ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator(appContext.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public IUserRepository getUserRepository() {
        if (userRepository == null) {
            // Costruttore stub senza AuthStore
            userRepository = new UserRepository();
        }
        return userRepository;
    }
}
