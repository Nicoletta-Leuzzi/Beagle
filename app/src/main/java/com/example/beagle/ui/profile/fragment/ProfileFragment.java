package com.example.beagle.ui.profile.fragment;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.beagle.R;
import com.example.beagle.model.Pet;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileFragment extends Fragment {

    private TextInputEditText name, species, breed, age;
    private ConstraintLayout btns_save_cancel;

    Pet pet;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pet = new Pet("", "", "", "", "", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        name = view.findViewById(R.id.outlinedTextFieldName);
        species = view.findViewById(R.id.outlinedTextFieldSpecies);
        breed = view.findViewById(R.id.outlinedTextFieldBreed);
        age = view.findViewById(R.id.outlinedTextFieldAge);
        btns_save_cancel = view.findViewById(R.id.btns_save_cancel);
        Button btnSave = view.findViewById(R.id.btn_save);
        Button btnCancel = view.findViewById(R.id.btn_cancel);

        name.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                btns_save_cancel.setVisibility(VISIBLE);
            }
        });

        species.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                btns_save_cancel.setVisibility(VISIBLE);
            }
        });

        breed.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                btns_save_cancel.setVisibility(VISIBLE);
            }
        });

        age.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                btns_save_cancel.setVisibility(VISIBLE);
            }
        });

        btnSave.setOnClickListener(v -> {
                pet.setName(name.getText().toString());
                pet.setSpecies(species.getText().toString());
                pet.setBreed(breed.getText().toString());
                pet.setAge(age.getText().toString());
                btns_save_cancel.setVisibility(GONE);

        });
        btnCancel.setOnClickListener(v-> {
            name.setText(pet.getName());
            species.setText(pet.getSpecies());
            breed.setText(pet.getBreed());
            age.setText(pet.getAge());
            btns_save_cancel.setVisibility(GONE);
        });

        return view;
    }
}































