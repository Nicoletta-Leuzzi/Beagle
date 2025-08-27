package com.example.beagle.model;

public class Pet {
    private String petId, userId, name, species, breed;
    private int age;

    public Pet(String petId, String userId, String name, String species, String breed, int age) {
        this.petId = petId;
        this.userId = userId;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
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
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }
}
