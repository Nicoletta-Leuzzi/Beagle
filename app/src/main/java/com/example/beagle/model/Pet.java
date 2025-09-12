package com.example.beagle.model;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "idToken",
                childColumns = "idToken",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        ),
        indices = { @Index(value = {"idToken", "name"}, unique = true),
                @Index("idToken") }
)
public class Pet {
    @PrimaryKey(autoGenerate = true)
    private long petId; // autogenerato da Room
    private String idToken, name, breed, age;
    private byte species; // TODO: da rivedere utilizzo
    private long birthDate;

    public Pet(long petId, @NonNull String idToken, @NonNull String name, @NonNull byte species, String breed, long birthDate) {
        this.petId = petId;
        this.idToken = idToken;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.birthDate = birthDate;
        this.age = calculateAge(birthDate);
    }

    // Costruttore di comodo per creare un nuovo Pet prima dell'inserimento (senza id)
    @Ignore
    public Pet(@NonNull String idToken, @NonNull String name,
               @NonNull byte species, String breed, long birthDate) {
        this.idToken = idToken;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.birthDate = birthDate;
        // petId rimane 0 -> Room lo autogenera all'insert
    }

    @Ignore
    public Pet(Pet other){
        this.petId = other.petId;
        this.idToken = other.idToken;
        this.name = other.name;
        this.species = other.species;
        this.breed = other.breed;
        this.birthDate = other.birthDate;
        this.age = other.age;
    }

    @Ignore
    public Pet(String name) {
        this.name = name;
    }

    @Ignore
    public Pet() {
    }

    public String getAge() {
        return age;
    }

    public long getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(long birthDate) {
        this.birthDate = birthDate;
        this.age=calculateAge(getBirthDate());
    }

    public String getidToken() {
        return idToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPetId() {
        return petId;
    }

    public String getSpeciesString() {
        if(species == 0)
            return "Cane";
        else
            return "Gatto";
    }

    public void setSpecies(String species) {

        if(species.equalsIgnoreCase("Cane"))
            this.species = 0;
        if(species.equalsIgnoreCase("Gatto"))
            this.species = 1;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    private String calculateAge(long birthDateMillis) {
        Calendar birth = Calendar.getInstance();
        birth.setTime(new Date(birthDateMillis));

        Calendar today = Calendar.getInstance();

        int years = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);

        if (today.get(Calendar.MONTH) < birth.get(Calendar.MONTH) ||
                (today.get(Calendar.MONTH) == birth.get(Calendar.MONTH) &&
                        today.get(Calendar.DAY_OF_MONTH) < birth.get(Calendar.DAY_OF_MONTH))) {
            years--;
        }

        return years+"";
    }

    public String toString(){
        return getName();
    }

    public boolean equals(Pet other){
        return (this.name).equals(other.getName());
    }








    public void setPetId(long petId) {
        this.petId = petId;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public byte getSpecies() {
        return this.species;
    }
    public void setSpecies(byte species) {
        this.species = species;
    }
}
