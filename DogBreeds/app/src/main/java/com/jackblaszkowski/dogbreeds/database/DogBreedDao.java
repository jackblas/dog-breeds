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
}
