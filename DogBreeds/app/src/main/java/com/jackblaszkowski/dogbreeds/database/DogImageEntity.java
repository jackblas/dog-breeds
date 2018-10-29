package com.jackblaszkowski.dogbreeds.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "images_table")
public class DogImageEntity {


    @PrimaryKey
    @NonNull
    private String url;
    @NonNull
    private String breed;
    @NonNull
    private String subBreed;

    public DogImageEntity(@NonNull String url, @NonNull String breed, @NonNull String subBreed) {
        this.url = url;
        this.breed = breed;
        this.subBreed = subBreed;
    }

    @NonNull
    public String getUrl() {
        return url;
    }

    @NonNull
    public String getBreed() {
        return breed;
    }

    @NonNull
    public String getSubBreed() {
        return subBreed;
    }

}
