package com.example.beagle.ui.chat.fragment;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.example.beagle.R;
import com.example.beagle.model.Pet;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private TextInputEditText name, breed, birthDate, age;
    private Button btnSave, btnCancel, btnAdd, btnDelete, btnSetings;
    private ConstraintLayout btns_save_cancel;
    private SimpleDateFormat sdf;
    private MaterialAutoCompleteTextView autoCompletePet, autoCompleteSpecies;
    private TextInputLayout autoCompletePetLayout, autoCompleteSpeciesLayout, nameLayout, speciesLayout, breedLayout, birthDateLayout, ageLayout;
    private Pet pet;
    private boolean fieldsError;
    private List<Pet> animals = new ArrayList<>();
    private final List<String> species = new ArrayList<>();
    // 0 = cane, 1 = gatto
    private ArrayAdapter<Pet> petAdapter;
    private ArrayAdapter<String> speciesAdapter;

    private int years = 0, remainingMonths = 0;

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
        autoCompleteSpecies = view.findViewById(R.id.autoCompleteSpecies);
        speciesLayout = view.findViewById(R.id.speciesLayoutDropDownMenu);
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
        btnSetings = view.findViewById(R.id.btnSettings);
        autoCompletePet = view.findViewById(R.id.outlinedTextFieldDropDownMenu);
        autoCompletePetLayout = view.findViewById(R.id.textInputLayoutDropDownMenu);
        autoCompleteSpeciesLayout = view.findViewById(R.id.speciesLayoutDropDownMenu);

        fieldsError = false;

        petAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, animals);
        autoCompletePet.setAdapter(petAdapter);

        speciesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, species);
        autoCompleteSpecies.setAdapter(speciesAdapter);
        species.add("Cane");
        species.add("Gatto");
        speciesAdapter.notifyDataSetChanged();

        if(animals.isEmpty()){
            disableDropDownMenu();
            btnDelete.setVisibility(INVISIBLE);
        }

        btnSave.setOnClickListener(v -> {

            fieldsError = false;
            if(name.getText().toString().isEmpty()){
                nameLayout.setError("Obbligatorio");
                fieldsError = true;
            }

            if(autoCompleteSpecies.getText().toString().isEmpty()){
                speciesLayout.setError("Obbligatorio");
                fieldsError = true;
            }

            if(breed.getText().toString().isEmpty()){
                breedLayout.setError("Obbligatorio");
                fieldsError = true;
            }

            if(birthDate.getText().toString().isEmpty()){
                birthDateLayout.setError("Obbligatorio");
                fieldsError = true;
            }

            if(!fieldsError) {
                try {
                    byte speciesCode = mapSpeciesToCode(autoCompleteSpecies.getText().toString());
                    pet = new Pet(
                            "",
                            name.getText().toString(),
                            speciesCode,
                            breed.getText().toString(),
                            sdf.parse(birthDate.getText().toString()).getTime());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                animals.add(pet);
                petAdapter.notifyDataSetChanged();
                disableAllInputText();
                if (animals.isEmpty()) {
                    disableDropDownMenu();
                } else {
                    enableDropDownMenu();
                    autoCompletePet.setText(pet.toString(), false);
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
                autoCompleteSpecies.setText(pet.getSpeciesString(),false);
                breed.setText(pet.getBreed());
                if (pet.getBirthDate() != 0) {
                    birthDate.setText(sdf.format(new Date(pet.getBirthDate())));
                }
                autoCompletePet.setText(pet.toString(), false);
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

        autoCompletePet.setOnItemClickListener(
                (parent, v, position, id) -> {

            pet = (Pet) parent.getItemAtPosition(position);
            name.setText(pet.getName());
            autoCompleteSpecies.setText(pet.getSpeciesString(),false);
            breed.setText(pet.getBreed());

            if (pet.getBirthDate() != 0) {
                birthDate.setText(sdf.format(new Date(pet.getBirthDate())));
            }
//            age.setText(pet.getAge());
            btnDelete.setVisibility(VISIBLE);
        });


        birthDate.setOnClickListener(v -> showDatePicker());

        birthDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.toString().isEmpty()) {
                    birthDateLayout.setError(null);
                    try {
                        years = (calculateAge(sdf.parse(birthDate.getText().toString()).getTime())/12);
                        remainingMonths = (calculateAge(sdf.parse(birthDate.getText().toString()).getTime())%12);

                        StringBuilder sb = new StringBuilder();

                        // Gestione anni
                        if (years > 0) {
                            sb.append(years).append(years == 1 ? " Year" : " Years");
                        }

                        // Gestione mesi (solo se > 0)
                        if (remainingMonths > 0) {
                            if (sb.length() > 0) sb.append(" "); // aggiunge spazio se c’è già "Year"
                            sb.append(remainingMonths).append(remainingMonths == 1 ? " Month" : " Months");
                        }

                        // Se entrambi 0 (caso raro: oggi è la data di nascita)
                        if (sb.length() == 0) {
                            sb.append("0 Months");
                        }

                        age.setText(sb.toString());

                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        autoCompleteSpecies.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.toString().isEmpty()) {
                    speciesLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!s.toString().isEmpty()) {
                    nameLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        breed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!s.toString().isEmpty()) {
                    breedLayout.setError(null);
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
                autoCompletePetLayout.setEnabled(false);
            }
        });

        autoCompletePetLayout.setEndIconOnClickListener(null);
        autoCompleteSpeciesLayout.setEndIconOnClickListener(null);

        //DA ATIVARE SE I DROPDOWN VANNO IN CONFLITTO CON IL CAMBIO TEMA
//        autoCompletePet.setOnClickListener(v-> {
//            if (!autoCompletePet.getText().toString().isEmpty())
//                petAdapter.getFilter().filter(null);
//        });
//
//        autoCompleteSpecies.setOnClickListener(v-> {
//            if (!autoCompleteSpecies.getText().toString().isEmpty())
//                speciesAdapter.getFilter().filter(null);
//        });

        btnSetings.setOnClickListener(v->{
            Navigation.findNavController(v)
                    .navigate(R.id.action_profileFragment_to_settingsFragment);
        });


        return view;
    }






// METODI
    private void showDatePicker() {
        // Costruzione del MaterialDatePicker
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select Birth Date");

        // Imposto il vincolo di non poter scegliere date future
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(DateValidatorPointBackward.now());
        builder.setCalendarConstraints(constraintsBuilder.build());

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
        datePicker.addOnPositiveButtonClickListener
        (new MaterialPickerOnPositiveButtonClickListener<Long>() {
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
        autoCompletePetLayout.setEnabled(false);
    }

    private void enableDropDownMenu(){
        autoCompletePetLayout.setEnabled(true);
    }

    private void clearAllFields(){
        autoCompletePet.setText("", false);
        name.setText("");
        autoCompleteSpecies.setText("", false);
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
        petAdapter.notifyDataSetChanged();
    }

    private void resetErrors(){
        nameLayout.setError(null);
        speciesLayout.setError(null);
        breedLayout.setError(null);
        birthDateLayout.setError(null);
    }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private int calculateAge(long birthTimestamp) {
            // Converte il timestamp in LocalDate
            LocalDate birthDate = Instant.ofEpochMilli(birthTimestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            LocalDate today = LocalDate.now();

            // Calcola mesi totali tra anni e mesi
            int months = (today.getYear() - birthDate.getYear()) * 12
                    + (today.getMonthValue() - birthDate.getMonthValue());

            // Se il giorno odierno è prima del giorno di nascita, sottrai 1 mese
            if (today.getDayOfMonth() < birthDate.getDayOfMonth()) {
                months--;
            }

            // Sicurezza: non avere mesi negativi
            months = Math.max(months, 0);

            return months;
        }


    private byte mapSpeciesToCode(String label) {
        if (label == null) return -1;
        String l = label.trim().toLowerCase(Locale.ITALIAN);
        if (l.startsWith("cane")) return 0;
        if (l.startsWith("gatto")) return 1;
        return -1;                             // Sconosciuto
    }
}































