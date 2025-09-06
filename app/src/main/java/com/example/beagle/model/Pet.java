package com.example.beagle.model;

import java.util.Calendar;
import java.util.Date;

public class Pet {
    private String petId, userId, name, breed, age;
    private Byte species;
    private long birthDate;

    public Pet(String petId, String userId, String name, String species, String breed, long birthDate) {
        this.petId = petId;
        this.userId = userId;
        this.name = name;
        if(species.equalsIgnoreCase("Cane"))
            this.species = 0;
        if(species.equalsIgnoreCase("Gatto"))
            this.species = 1;
        this.breed = breed;
        this.birthDate = birthDate;
        this.age = calculateAge(birthDate);
    }

    public Pet(Pet other){
        this.petId = other.petId;
        this.userId = other.userId;
        this.name = other.name;
        this.species = other.species;
        this.breed = other.breed;
        this.birthDate = other.birthDate;
        this.age = other.age;
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

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPetId() {
        return petId;
    }

    public String getSpecies() {
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
}
