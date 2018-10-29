package com.jackblaszkowski.dogbreeds.database;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

@Entity(tableName = "breed_table", primaryKeys = {"breed", "subBreed"})
public class DogBreedEntity {


    @NonNull
    private String breed;
    @NonNull
    private String subBreed;
    private String urlOne;
    private String urlTwo;
    private String urlThree;

    public DogBreedEntity(@NonNull String breed, @NonNull String subBreed, String urlOne, String urlTwo, String urlThree) {
        this.breed = breed;
        this.subBreed = subBreed;
        this.urlOne = urlOne;
        this.urlTwo = urlTwo;
        this.urlThree = urlThree;
    }

    @NonNull
    public String getBreed() {
        return breed;
    }

    @NonNull
    public String getSubBreed() {
        return subBreed;
    }

    public String getUrlOne() {
        return urlOne;
    }

    public String getUrlTwo() {
        return urlTwo;
    }

    public String getUrlThree() {
        return urlThree;
    }

}
