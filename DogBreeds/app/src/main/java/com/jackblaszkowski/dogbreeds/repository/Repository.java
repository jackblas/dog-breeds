package com.jackblaszkowski.dogbreeds.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public abstract class Repository<ResultType> {

    private final static String LOG_TAG = "Repository";

    MediatorLiveData<Resource<ResultType>> result;

    public LiveData<Resource<ResultType>> getResource(){

        result = new MediatorLiveData<>();

        result.setValue(Resource.loading(null));

        LiveData<ResultType> dbSource = loadFromDb();

        result.addSource(dbSource, data -> {

            result.removeSource(dbSource);

            if(shouldFetch(data)) {
                Log.d(LOG_TAG, "In getResource() - Fetch new data from the server <=== ");
                fetchFromNetwork(dbSource);
            } else {
                Log.d(LOG_TAG, "In getResource() - Load existing data from the db <=== ");

                result.addSource(dbSource, newData -> setValue(Resource.success(newData)));
            }

        });

        return result;
    }


    protected void setValue(Resource<ResultType> newValue) {
        result.setValue(newValue);
    }

    protected abstract void fetchFromNetwork(final LiveData<ResultType> dbSource);

    @NonNull
    protected abstract LiveData<ResultType> loadFromDb();

    protected abstract boolean shouldFetch(@Nullable ResultType data);

}
