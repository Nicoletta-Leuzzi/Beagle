package com.example.beagle.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

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
    private String name;

    public Pet() {
    }

    public Pet(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
