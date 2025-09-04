package com.example.beagle.ui.profile.fragment;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.os.Build;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.example.beagle.R;
import com.example.beagle.model.Pet;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ProfileFragment extends Fragment {

    private TextInputEditText name, species, breed, birthDate, age;
    private Button btnSave, btnCancel, btnAdd, btnDelete;
    private ConstraintLayout btns_save_cancel;
    private SimpleDateFormat sdf;
    private AutoCompleteTextView autoCompleteTextView;
    private TextInputLayout textInputLayoutAutoCompleteTextView, nameLayout, speciesLayout, breedLayout, birthDateLayout, ageLayout;
    private Pet pet, tempPet;
    private int indexOfPet;
    private boolean fieldsError;
    private List<Pet> animals = new ArrayList<>();
    private ArrayAdapter<Pet> adapter;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        name = view.findViewById(R.id.outlinedTextFieldName);
        nameLayout = view.findViewById(R.id.nameLayout);
        species = view.findViewById(R.id.outlinedTextFieldSpecies);
        speciesLayout = view.findViewById(R.id.speciesLayout);
        breed = view.findViewById(R.id.outlinedTextFieldBreed);
        breedLayout = view.findViewById(R.id.breedLayout);
        birthDate = view.findViewById(R.id.outlinedTextFieldBirthDate);
        birthDateLayout = view.findViewById(R.id.birthDateLayout);
        age = view.findViewById(R.id.outlinedTextFieldAge);
        ageLayout = view.findViewById(R.id.ageLayout);
        btns_save_cancel = view.findViewById(R.id.btns_save_cancel);
        btnSave = view.findViewById(R.id.btn_save);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnAdd = view.findViewById(R.id.btnAdd);
        btnDelete = view.findViewById(R.id.btn_delete);
        autoCompleteTextView = view.findViewById(R.id.outlinedTextFieldDropDownMenu);
        textInputLayoutAutoCompleteTextView = view.findViewById(R.id.textInputLayoutDropDownMenu);

        fieldsError = false;
        adapter = new ArrayAdapter<Pet>(getContext(), android.R.layout.simple_dropdown_item_1line, animals);
        autoCompleteTextView.setAdapter(adapter);

        if(animals.isEmpty()){
            disableDropDownMenu();
            btnDelete.setVisibility(INVISIBLE);
        }

        btnSave.setOnClickListener(v -> {

            fieldsError = false;
            if(name.getText().toString().isEmpty()){
                name.setError("Campo obbligatorio");
                fieldsError = true;
            }
//            else{
//                name.setError(null);
//            }

            if(species.getText().toString().isEmpty()){
                species.setError("Campo obbligatorio");
                fieldsError = true;
            }
//            else{
//                species.setError(null);
//            }

            if(breed.getText().toString().isEmpty()){
                breed.setError("Campo obbligatorio");
                fieldsError = true;
            }
            else{
                breed.setError(null);
            }

            if(birthDate.getText().toString().isEmpty()){
                birthDate.setError("Campo obbligatorio");
                fieldsError = true;
            }
//            else{
//                birthDate.setError(null);
//            }

            if(!fieldsError) {
                try {
                    pet = new Pet("", "", name.getText().toString(), species.getText().toString(), breed.getText().toString(), sdf.parse(birthDate.getText().toString()).getTime());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                age.setText(pet.getAge());
                animals.add(pet);
                adapter.notifyDataSetChanged();
                disableAllInputText();
                if (animals.isEmpty()) {
                    disableDropDownMenu();
                } else {
                    enableDropDownMenu();
                    autoCompleteTextView.setText(pet.toString(), false);
                }
                btnAdd.setVisibility(VISIBLE);
                btnDelete.setVisibility(VISIBLE);
                btns_save_cancel.setVisibility(INVISIBLE);
            }
        else {
            Snackbar.make(view, "Compila tutti i campi", Snackbar.LENGTH_SHORT).show();
        }
        });

        btnCancel.setOnClickListener(v-> {
            if(!(animals.isEmpty())) {
                name.setText(pet.getName());
                species.setText(pet.getSpecies());
                breed.setText(pet.getBreed());
                if (pet.getBirthDate() != 0) {
                    birthDate.setText(sdf.format(new Date(pet.getBirthDate())));
                }
                age.setText(pet.getAge());
                autoCompleteTextView.setText(pet.toString(), false);
                btnDelete.setVisibility(VISIBLE);
                enableDropDownMenu();
            }
                else{
                    clearAllFields();
                    disableDropDownMenu();
                }
            resetErrors();
            disableAllInputText();
            btnAdd.setVisibility(VISIBLE);
            btns_save_cancel.setVisibility(INVISIBLE);
        });


//        autoCompleteTextView.setOnClickListener(v->{
//
//                autoCompleteTextView.showDropDown();
//        });

        autoCompleteTextView.setOnItemClickListener((parent, v, position, id) -> {

            pet = (Pet) parent.getItemAtPosition(position);
            name.setText(pet.getName());
            species.setText(pet.getSpecies());
            breed.setText(pet.getBreed());

            if (pet.getBirthDate() != 0) {
                birthDate.setText(sdf.format(new Date(pet.getBirthDate())));
            }
            age.setText(pet.getAge());
            btnDelete.setVisibility(VISIBLE);
        });


        birthDate.setOnClickListener(v -> showDatePicker());

        birthDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.toString().isEmpty()) {
                    birthDate.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnAdd.setOnClickListener(v->{
            btnAdd.setVisibility(INVISIBLE);
            btns_save_cancel.setVisibility(VISIBLE);
            btnDelete.setVisibility(INVISIBLE);
            disableDropDownMenu();
            clearAllFields();
            enableAllInputText();
        });

        btnDelete.setOnClickListener(v->{
            deletePet(pet);
            clearAllFields();
            btnDelete.setVisibility(INVISIBLE);
            if(animals.isEmpty()){
                textInputLayoutAutoCompleteTextView.setEnabled(false);
            }
        });

        return view;
    }















// METODI
    private void showDatePicker() {
        // Costruzione del MaterialDatePicker
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select Birth Date");

        // Se c’è già una data scritta, impostala come selezione iniziale
        String currentText = birthDate.getText() != null ? birthDate.getText().toString() : "";
        if (!currentText.isEmpty()) {
            try {
                Date parsedDate = sdf.parse(currentText);
                if (parsedDate != null) {
                    builder.setSelection(parsedDate.getTime());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        final MaterialDatePicker<Long> datePicker = builder.build();

        // Callback sul pulsante OK
        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                String formattedDate = sdf.format(new Date(selection));
                birthDate.setText(formattedDate);
            }
        });

        // Callback su Cancel
        datePicker.addOnNegativeButtonClickListener(dialog -> birthDate.clearFocus());

        // Callback su chiusura del popup
        datePicker.addOnDismissListener(dialog -> birthDate.clearFocus());

        // Mostro il calendario
        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private void enableAllInputText(){
        nameLayout.setEnabled(true);
        speciesLayout.setEnabled(true);
        breedLayout.setEnabled(true);
        birthDateLayout.setEnabled(true);
    }

    private void disableAllInputText(){
        nameLayout.setEnabled(false);
        speciesLayout.setEnabled(false);
        breedLayout.setEnabled(false);
        birthDateLayout.setEnabled(false);
    }

    private void disableDropDownMenu(){
        textInputLayoutAutoCompleteTextView.setEnabled(false);
    }

    private void enableDropDownMenu(){
        textInputLayoutAutoCompleteTextView.setEnabled(true);
    }

    private void clearAllFields(){
        autoCompleteTextView.setText("", false);
        name.setText("");
        species.setText("");
        breed.setText("");
        birthDate.setText("");
        age.setText("");
    }

    private void deletePet (Pet petToBeDeleted){
        if(!animals.isEmpty()){
            animals.remove(petToBeDeleted);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                pet = animals.getLast();
            }

        }
        adapter.notifyDataSetChanged();
    }

    private void resetErrors(){
        name.setError(null);
        species.setError(null);
        breed.setError(null);
        birthDate.setError(null);
    }
}































