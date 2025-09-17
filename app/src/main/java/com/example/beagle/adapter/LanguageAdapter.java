package com.example.beagle.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.example.beagle.R;

import java.util.ArrayList;
import java.util.List;

public class LanguageAdapter extends ArrayAdapter<String> {

    private final List<String> languages = new ArrayList<>();

    public LanguageAdapter(@NonNull Context context) {
        super(context, android.R.layout.simple_dropdown_item_1line);

        languages.add(context.getString(R.string.english));
        languages.add(context.getString(R.string.italian));

        addAll(languages);
    }

    public List<String> getLanguages() {
        return languages;
    }
}

