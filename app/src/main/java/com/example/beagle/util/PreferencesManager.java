package com.example.beagle.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String PREF_NAME = "user_settings";
    private static final String KEY_THEME = "theme";
    private static final String KEY_LANGUAGE = "language";

    private final SharedPreferences prefs;

    public PreferencesManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // --- THEME ---
    public void saveTheme(int mode) {
        prefs.edit().putInt(KEY_THEME, mode).apply();
    }

    public int getTheme() {
        return prefs.getInt(KEY_THEME, -1); // -1 = non ancora scelto
    }

    // --- LANGUAGE ---
    public void saveLanguage(String langTag) {
        prefs.edit().putString(KEY_LANGUAGE, langTag).apply();
    }

    public String getLanguage() {
        return prefs.getString(KEY_LANGUAGE, ""); // "" = non ancora scelto
    }
}
