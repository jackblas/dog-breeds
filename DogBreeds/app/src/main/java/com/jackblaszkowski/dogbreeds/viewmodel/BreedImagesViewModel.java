package com.jackblaszkowski.dogbreeds.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.jackblaszkowski.dogbreeds.AppDataRepository;

import java.util.List;

public class BreedImagesViewModel extends AndroidViewModel {

    private AppDataRepository mRepository;
    private MutableLiveData<Pair<String,String>> breedName = new MutableLiveData<>();
    private LiveData<List<String>> mDogBreedMorePictures;

    public BreedImagesViewModel(@NonNull Application application) {
        super(application);
        mRepository = AppDataRepository.getInstance(application);

        mDogBreedMorePictures = Transformations.switchMap(breedName, (Pair<String,String> breedName) -> {
            return mRepository.getMoreImages(breedName.first, breedName.second);
        });

    }

    public void setBreed(String breed, String subBreed){
        Pair<String,String> breedName = Pair.create(breed,subBreed);
        this.breedName.setValue(breedName);
    }

    public LiveData<List<String>> getMoreImages() {
        return mDogBreedMorePictures;
    }
}
