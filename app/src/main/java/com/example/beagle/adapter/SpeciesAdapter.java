package com.example.beagle.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class SpeciesAdapter extends ArrayAdapter<String> {

    public SpeciesAdapter(@NonNull Context context, @NonNull List<String> species) {
        super(context, android.R.layout.simple_dropdown_item_1line, species);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        String species = getItem(position);
        if (species != null) {
            view.setText(species);
        }
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}

