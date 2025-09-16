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

    // Database
    public static final int DATABASE_VERSION = 1;
    public static final String SAVED_DATABASE = "saved_db";
    public static final String FIREBASE_REALTIME_DATABASE = "https://beagle-3a3fc-default-rtdb.europe-west1.firebasedatabase.app/";
    public static final String FIREBASE_USERS_COLLECTION = "users";
    public static final String FIREBASE_PET_COLLECTION = "pets";
    public static final String FIREBASE_CONVERSATION_COLLECTION = "conversations";
    public static final String FIREBASE_MESSAGES_COLLECTION = "messages";



    public static final String CONVERSATION_BUNDLE_KEY = "current_conversation";
    public static final String CONVERSATION_DELETED_BUNDLE_KEY = "deleted_conversation";
    public static final String PET_BUNDLE_KEY = "current_pet";

    // API
    public static final String API_URL = "https://api.compactif.ai/v1/chat/completions";
    public static final String SAMPLE_JSON_FILENAME = "sample_api_response.json";
    public static final String MODEL = "gpt-oss-120b";



}
