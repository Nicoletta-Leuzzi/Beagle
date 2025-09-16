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
        themeSwitch = view.findViewById(R.id.switchTheme);
        textInputLayoutLanguage = view.findViewById(R.id.textInputLayoutLanguage);
        autoCompleteLanguage = view.findViewById(R.id.materialAutoCompleteTextViewLanguage);
        btnLogout = view.findViewById(R.id.btnLogout);

        languages.add(getString(R.string.english));
        languages.add(getString(R.string.italian));
        languageAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, languages);
        autoCompleteLanguage.setAdapter(languageAdapter);

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            autoCompleteLanguage.dismissDropDown();
            autoCompleteLanguage.clearFocus();
            if (isChecked) {

                // Tema scuro
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                themeSwitch.setThumbIconResource(R.drawable.dark_mode);
            } else {
                // Tema chiaro
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                themeSwitch.setThumbIconResource(R.drawable.light_mode);
            }
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

            String selected = (String) parent.getItemAtPosition(position);

            if (selected.equalsIgnoreCase(getString(R.string.english))) {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en"));
            } else if (selected.equalsIgnoreCase(getString(R.string.italian))) {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("it"));
            }
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

}



















