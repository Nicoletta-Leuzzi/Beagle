package com.example.beagle.ui.chat.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.beagle.R;
import com.example.beagle.ui.welcome.WelcomeActivity;
import com.example.beagle.util.PreferencesManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private MaterialSwitch themeSwitch;
    private TextInputLayout textInputLayoutLanguage;
    private MaterialAutoCompleteTextView autoCompleteLanguage;
    private List<String> languages = new ArrayList<>();
    private ArrayAdapter<String> languageAdapter;
    private MaterialButton btnLogout;


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        PreferencesManager prefs = new PreferencesManager(requireContext());
        themeSwitch = view.findViewById(R.id.switchTheme);
        textInputLayoutLanguage = view.findViewById(R.id.textInputLayoutLanguage);
        autoCompleteLanguage = view.findViewById(R.id.materialAutoCompleteTextViewLanguage);
        btnLogout = view.findViewById(R.id.btnLogout);

        languages.add(getString(R.string.english));
        languages.add(getString(R.string.italian));
        languageAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, languages);
        autoCompleteLanguage.setAdapter(languageAdapter);

        updateUi();

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            autoCompleteLanguage.dismissDropDown();
            autoCompleteLanguage.clearFocus();
            int mode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            AppCompatDelegate.setDefaultNightMode(mode);
            prefs.saveTheme(mode);
        });

        textInputLayoutLanguage.setEndIconOnClickListener(null);

        autoCompleteLanguage.setOnClickListener(v -> {
            if (!autoCompleteLanguage.getText().toString().isEmpty()) {
                languageAdapter.getFilter().filter(null);
            }
        });

        autoCompleteLanguage.setOnItemClickListener((parent, v, position, id) -> {
            autoCompleteLanguage.dismissDropDown();
            autoCompleteLanguage.clearFocus();

            String langTag;
            if (position == 0) {
                langTag = "en";
            } else {
                langTag = "it";
            }
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(langTag));
            prefs.saveLanguage(langTag);
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            Activity ctx = requireActivity();
            Intent i = new Intent(ctx, WelcomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.putExtra("from_logout", true);

            startActivity(i);
            ctx.finish();
        });


        return view;
    }


//METODI

    private void updateUi() {
        PreferencesManager prefs = new PreferencesManager(requireContext());

        // --- Tema ---
        int savedTheme = prefs.getTheme();
        boolean isNight;

        if (savedTheme == -1) {
            // Primo avvio: rilevo tema di sistema
            int currentNightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
            if (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
                isNight = true;
                savedTheme = AppCompatDelegate.MODE_NIGHT_YES;
            } else {
                isNight = false;
                savedTheme = AppCompatDelegate.MODE_NIGHT_NO;
            }
            prefs.saveTheme(savedTheme);
        } else {
            isNight = savedTheme == AppCompatDelegate.MODE_NIGHT_YES;
        }

        themeSwitch.setChecked(isNight);
        AppCompatDelegate.setDefaultNightMode(savedTheme);

        // Cambio icona thumb dello switch
        if (isNight) {
            themeSwitch.setThumbIconDrawable(getResources().getDrawable(R.drawable.dark_mode, null));
        } else {
            themeSwitch.setThumbIconDrawable(getResources().getDrawable(R.drawable.light_mode, null));
        }

        // --- Lingua ---
        String savedLang = prefs.getLanguage();
        if (savedLang.isEmpty()) {
            // Primo accesso: prendo lingua di sistema
            savedLang = getResources().getConfiguration().getLocales().get(0).getLanguage();
            prefs.saveLanguage(savedLang);
        }

        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(savedLang));

        if ("en".equals(savedLang)) {
            autoCompleteLanguage.setText(getString(R.string.english), false);
        } else if ("it".equals(savedLang)) {
            autoCompleteLanguage.setText(getString(R.string.italian), false);
        }
    }





}



















