package com.jackblaszkowski.dogbreeds.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface DogBreedDao {

    @Query("SELECT * from breed_table ORDER BY breed ASC, subBreed ASC")
    LiveData<List<DogBreedEntity>> loadBreeds();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DogBreedEntity dogBreedEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<DogBreedEntity> dogBreedEntities);

    @Query("DELETE FROM breed_table")
    void deleteAll();

    // images_table

    @Query("SELECT * from images_table WHERE breed = :breed AND subBreed = :subBreed")
    LiveData<List<DogImageEntity>> loadBreedImages(String breed, String subBreed);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertImage(DogImageEntity dogImageEntity);

    @Query("DELETE FROM images_table")
    void deleteAllImages();
}
