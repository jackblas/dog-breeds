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
    LiveData<List<DogBreedEntity>> loadMainActivityPictures();

    @Query("SELECT * FROM breed_table LIMIT 1")
    DogBreedEntity hasData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DogBreedEntity dogBreedEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<DogBreedEntity> dogBreedEntities);

    @Query("DELETE FROM breed_table")
    void deleteAll();

    // image_table

    @Query("SELECT url from images_table WHERE breed = :breed AND subBreed = :subBreed")
    LiveData<List<String>> loadBreedImages(String breed, String subBreed);

    @Query("SELECT * FROM images_table WHERE breed = :breed AND subBreed = :subBreed LIMIT 1")
    DogImageEntity hasImages(String breed, String subBreed);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertImage(DogImageEntity dogImageEntity);


    @Query("DELETE FROM images_table")
    void deleteAllImages();
}
