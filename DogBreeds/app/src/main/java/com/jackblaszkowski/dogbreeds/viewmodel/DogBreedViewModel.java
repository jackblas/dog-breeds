package com.jackblaszkowski.dogbreeds.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.jackblaszkowski.dogbreeds.AppDataRepository;
import com.jackblaszkowski.dogbreeds.database.DogBreedEntity;

import java.util.List;

public class DogBreedViewModel extends AndroidViewModel {

    private AppDataRepository mRepository;

    private LiveData<List<DogBreedEntity>> mDogBreedEntities;

    private LiveData<List<String>> mDogBreedMorePictures;

    public DogBreedViewModel(Application application) {
        super(application);
        mRepository = AppDataRepository.getInstance(application);
    }

    public void refreshData() {
        mRepository.refreshData();
    }

    public LiveData<List<DogBreedEntity>> getDogBreeds() {
        mDogBreedEntities = mRepository.getAllBreeds();
        return mDogBreedEntities;
    }

    public LiveData<List<String>> getMoreImages(String breed, String subBreed) {
        mDogBreedMorePictures = mRepository.getMoreImages(breed, subBreed);
        return mDogBreedMorePictures;
    }


    //public void insert(DogBreedEntity dogBreedEntity) { mRepository.insert(dogBreedEntity); }
    //public void insertAll(List<DogBreedEntity> dogBreedEntityList) { mRepository.insertAll(dogBreedEntityList); }
}
