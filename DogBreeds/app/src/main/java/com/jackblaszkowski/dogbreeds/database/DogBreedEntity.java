package com.jackblaszkowski.dogbreeds.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
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

    @Ignore
    public DogBreedEntity() {
    }

    public DogBreedEntity(@NonNull String breed, @NonNull String subBreed, String urlOne, String urlTwo, String urlThree) {
        this.breed = breed;
        this.subBreed = subBreed;
        this.urlOne = urlOne;
        this.urlTwo = urlTwo;
        this.urlThree = urlThree;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getSubBreed() {
        return subBreed;
    }

    public void setSubBreed(String subBreed) {
        this.subBreed = subBreed;
    }

    public String getUrlOne() {
        return urlOne;
    }

    public void setUrlOne(String urlOne) {
        this.urlOne = urlOne;
    }

    public String getUrlTwo() {
        return urlTwo;
    }

    public void setUrlTwo(String urlTwo) {
        this.urlTwo = urlTwo;
    }

    public String getUrlThree() {
        return urlThree;
    }

    public void setUrlThree(String urlThree) {
        this.urlThree = urlThree;
    }
}
