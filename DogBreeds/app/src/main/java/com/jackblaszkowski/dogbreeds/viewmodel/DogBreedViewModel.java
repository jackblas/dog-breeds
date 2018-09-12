package com.jackblaszkowski.dogbreeds.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.jackblaszkowski.dogbreeds.AppDataRepository;
import com.jackblaszkowski.dogbreeds.database.DogBreedEntity;

import java.util.List;

public class DogBreedViewModel extends AndroidViewModel {

    private AppDataRepository mRepository;

    private LiveData<List<DogBreedEntity>> mDogBreedPictures;

    public DogBreedViewModel(Application application) {
        super(application);
        mRepository = AppDataRepository.getInstance(application);
    }

    public void refreshData() {
        mRepository.refreshData();
    }

    public LiveData<List<DogBreedEntity>> getDogBreedPictures() {
        mDogBreedPictures = mRepository.getAllBreedsPictures();
        return mDogBreedPictures;
    }


    //public void insert(DogBreedEntity dogBreedEntity) { mRepository.insert(dogBreedEntity); }
    //public void insertAll(List<DogBreedEntity> dogBreedEntityList) { mRepository.insertAll(dogBreedEntityList); }
}
