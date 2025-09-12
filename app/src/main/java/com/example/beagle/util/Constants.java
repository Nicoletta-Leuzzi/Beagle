package com.example.beagle.util;

/** Costanti usate per autenticazione (login/registrazione) e prefs. */
public final class Constants {
    private Constants() {
    }

    // --- Regole UI ---
    public static final int MINIMUM_LENGTH_PASSWORD = 6;

    // --- Errori normalizzati lato Auth (mappati dal DataSource Firebase) ---
    public static final String WEAK_PASSWORD_ERROR = "weak_password_error";
    public static final String USER_COLLISION_ERROR = "user_collision_error";
    public static final String INVALID_CREDENTIALS_ERROR = "invalid_credentials_error";
    public static final String INVALID_USER_ERROR = "invalid_user_error";
    public static final String UNEXPECTED_ERROR = "unexpected_error";
}
