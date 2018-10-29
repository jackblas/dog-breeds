package com.jackblaszkowski.dogbreeds.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.jackblaszkowski.dogbreeds.database.DogImageEntity;
import com.jackblaszkowski.dogbreeds.repository.DogImageRepository;
import com.jackblaszkowski.dogbreeds.repository.Resource;

import java.util.List;

public class BreedImagesViewModel extends AndroidViewModel {

    private DogImageRepository mRepository;
    private MutableLiveData<Pair<String,String>> breedName = new MutableLiveData<>();
    private LiveData<Resource<List<DogImageEntity>>> mDogBreedMorePictures;

    public BreedImagesViewModel(@NonNull Application application) {
        super(application);
        mRepository = DogImageRepository.getInstance(application);

        mDogBreedMorePictures = Transformations.switchMap(breedName, (Pair<String,String> breedName) -> {
                return  mRepository.loadMoreImages(breedName.first, breedName.second);
        });

    }

    public void setBreed(String breed, String subBreed){
        Pair<String,String> breedName = Pair.create(breed,subBreed);
        this.breedName.setValue(breedName);
    }

    public LiveData<Resource<List<DogImageEntity>>> getMoreImages() {
        return mDogBreedMorePictures;
    }
}
