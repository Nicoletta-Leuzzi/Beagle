package com.example.beagle.ui.profile.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.beagle.R;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private MaterialSwitch themeSwitch;
    private TextInputLayout textInputLayoutLanguage;
    private MaterialAutoCompleteTextView autoCompleteLanguage;
    private List<String> languages = new ArrayList<>();
    private ArrayAdapter<String> languageAdapter;


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

        languages.add("Inglese");
        languages.add("Italiano");
        languageAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, languages);
        autoCompleteLanguage.setAdapter(languageAdapter);
//        languageAdapter.notifyDataSetChanged();

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                autoCompleteLanguage.clearFocus();
                // Tema scuro
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                themeSwitch.setThumbIconResource(R.drawable.dark_mode);
            } else {
                autoCompleteLanguage.clearFocus();
                // Tema chiaro
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                themeSwitch.setThumbIconResource(R.drawable.light_mode);
            }
        });

        textInputLayoutLanguage.setEndIconOnClickListener(null);

//        textInputLayoutLanguage.setEndIconOnClickListener(v->{
//            if(!autoCompleteLanguage.getText().toString().isEmpty())
//                languageAdapter.getFilter().filter(null);
//            if(!autoCompleteLanguage.isPopupShowing()){
//                autoCompleteLanguage.showDropDown();
//            }
//            else{
//                autoCompleteLanguage.dismissDropDown();
//            }
//        });

        autoCompleteLanguage.setOnClickListener(v->{
            if(!autoCompleteLanguage.getText().toString().isEmpty())
            languageAdapter.getFilter().filter(null);
//            if(autoCompleteLanguage.isPopupShowing()){
//                autoCompleteLanguage.dismissDropDown();
//            }
//            else{
//                autoCompleteLanguage.showDropDown();
//            }
        });













        return view;
    }
}






//METODI






















