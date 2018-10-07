package com.jackblaszkowski.dogbreeds.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.jackblaszkowski.dogbreeds.AppDataRepository;
import com.jackblaszkowski.dogbreeds.database.DogBreedEntity;

import java.util.List;

public class DogBreedViewModel extends AndroidViewModel {

    private AppDataRepository mRepository;

    private MutableLiveData<Boolean> refresh = new MutableLiveData<>();
    private LiveData<List<DogBreedEntity>> mDogBreedEntities;

    public DogBreedViewModel(@NonNull Application application) {
        super(application);
        mRepository = AppDataRepository.getInstance(application);

        mDogBreedEntities = Transformations.switchMap(refresh, (refresh) -> {
            return mRepository.getAllBreeds(refresh);
        });

    }

    public LiveData<List<DogBreedEntity>> getDogBreeds() {
        return mDogBreedEntities;
    }

    public void setRefresh(boolean refresh) {
        this.refresh.setValue(refresh);

    }

}
