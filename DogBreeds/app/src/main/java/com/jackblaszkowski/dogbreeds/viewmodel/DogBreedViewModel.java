package com.jackblaszkowski.dogbreeds.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.jackblaszkowski.dogbreeds.database.DogBreedEntity;
import com.jackblaszkowski.dogbreeds.repository.DogBreedRepository;
import com.jackblaszkowski.dogbreeds.repository.Resource;

import java.util.List;

public class DogBreedViewModel extends AndroidViewModel {

    private DogBreedRepository mRepository;
    private MutableLiveData<Boolean> refresh = new MutableLiveData<>();
    private LiveData<Resource<List<DogBreedEntity>>> mDogBreedEntities;

    public DogBreedViewModel(@NonNull Application application) {
        super(application);
        mRepository = DogBreedRepository.getInstance(application);

        mDogBreedEntities = Transformations.switchMap(refresh, (refresh) -> {
            return  mRepository.loadDogBreeds(refresh);
        });
    }

    public LiveData<Resource<List<DogBreedEntity>>> getDogBreeds() {
        return mDogBreedEntities;
    }

    public void setRefresh(boolean refresh) {
        this.refresh.setValue(refresh);
    }
}
