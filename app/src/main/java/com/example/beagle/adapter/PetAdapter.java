package com.example.beagle.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.beagle.model.Pet;

import java.util.List;

public class PetAdapter extends ArrayAdapter<Pet> {

    public PetAdapter(@NonNull Context context, @NonNull List<Pet> pets) {
        super(context, android.R.layout.simple_dropdown_item_1line, pets);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        Pet pet = getItem(position);
        if (pet != null) {
            view.setText(pet.toString());
        }
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
