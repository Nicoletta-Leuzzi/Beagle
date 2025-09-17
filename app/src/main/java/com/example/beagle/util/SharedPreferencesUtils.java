package com.example.beagle.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {

    private static final String PREF_NAME = "user_settings";
    private static final String KEY_THEME = "theme";
    private static final String KEY_LANGUAGE = "language";

    private final SharedPreferences prefs;
    private final Context context;

    public SharedPreferencesUtils(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // --- Metodi generici originali ---
    public void writeStringData(String sharedPreferencesFileName, String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(sharedPreferencesFileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String readStringData(String sharedPreferencesFileName, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(sharedPreferencesFileName,
                Context.MODE_PRIVATE);
        return sharedPref.getString(key, null);
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
