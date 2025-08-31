package com.example.beagle.util;

import android.app.Application;
import android.content.Context;

import com.example.beagle.repository.user.IUserRepository;
import com.example.beagle.repository.user.UserRepository;
import com.example.beagle.source.user.BaseUserAuthenticationRemoteDataSource;
import com.example.beagle.source.user.UserAuthenticationFirebaseDataSource;

/**
 * ServiceLocator (stile prof) – versione minimale per AUTH (login/registrazione).
 * - Singleton: getInstance()
 * - Fornisce il repository utente tramite getUserRepository(Application)
 * - Nessuna parte "news"/Room/Retrofit.
 */
public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE;

    private IUserRepository userRepository;

    private ServiceLocator() { }

    /** Stile prof: nessun parametro. */
    public static ServiceLocator getInstance() {
        if (INSTANCE == null) {
            synchronized (ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator();
                }
            }
        }
        return INSTANCE;
    }

    /** Compat per vecchi punti chiamanti: ignora il parametro. */
    @Deprecated
    public static ServiceLocator getInstance(Context ignored) {
        return getInstance();
    }

    /**
     * Restituisce il repository utente (solo autenticazione).
     * Passa Application per coerenza col prof; qui non la usiamo.
     */
    public IUserRepository getUserRepository(Application application) {
        if (userRepository == null) {
            BaseUserAuthenticationRemoteDataSource authDs = new UserAuthenticationFirebaseDataSource();
            userRepository = new UserRepository(authDs);
        }
        return userRepository;
    }
}
