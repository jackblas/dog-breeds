package com.jackblaszkowski.dogbreeds.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jackblaszkowski.dogbreeds.api.BreedImagesResponse;
import com.jackblaszkowski.dogbreeds.api.DogApiClient;
import com.jackblaszkowski.dogbreeds.api.DogApiClientFactory;
import com.jackblaszkowski.dogbreeds.database.AppDatabase;
import com.jackblaszkowski.dogbreeds.database.DogBreedDao;
import com.jackblaszkowski.dogbreeds.database.DogImageEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DogImageRepository<ResultType> extends Repository<ResultType> {

    private final static String LOG_TAG = "DogImageRepository";

    private static DogImageRepository sInstance;
    private ConnectivityManager mConnMgr;
    private ExecutorService mExecutorService;

    private DogBreedDao mBreedDao;
    private String breed;
    private String subBreed;

    private DogImageRepository(Application application) {
        Log.d(LOG_TAG, "DogImageRepository Created");

        mExecutorService = Executors.newCachedThreadPool();
        mConnMgr = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);

        AppDatabase database = AppDatabase.getDatabase(application);
        mBreedDao = database.breedDao();
    }

    public static DogImageRepository getInstance(final Application application) {
        if (sInstance == null) {
            synchronized (DogImageRepository.class) {
                if (sInstance == null) {
                    sInstance = new DogImageRepository<List<DogImageEntity>>(application);
                }
            }
        }
        return sInstance;
    }

    public LiveData<Resource<ResultType>>  loadMoreImages(String breed, String subBreed) {
        Log.d(LOG_TAG, "In loadMoreImages() breed: " + breed + "-" + subBreed);

        this.breed=breed;
        this.subBreed=subBreed;

        return getResource();

    }

    @Override
    protected void fetchFromNetwork(final LiveData<ResultType> dbSource) {
        Log.d(LOG_TAG, "In fetchFromNetwork()");

        DogApiClient client = new DogApiClientFactory().getDogApiClient(DogApiClientFactory.CALL_IMAGES);
        Call<BreedImagesResponse> call;

        if(breed.equals(subBreed)) {
            call = client.getAllBreedImages(breed);

        } else {
            call = client.getAllSubBreedImages(breed,subBreed);
        }

        call.enqueue(new Callback<BreedImagesResponse>() {
            @Override
            public void onResponse(Call<BreedImagesResponse> call, Response<BreedImagesResponse> response) {
                BreedImagesResponse body = response.body();

                if(response.isSuccessful() && (body.getMessage() != null)) {

                    try {
                        String[] urls = body.getMessage();

                        int i = 0;
                        for (final String url : urls) {
                            i++;
                            Log.v(LOG_TAG, "Inserting a record for: " + breed + "-" + subBreed + " url= " +url);
                            // Some breeds have hundreds of images.
                            // To reduce network traffic, we load up to 24 images per breed
                            if(i > 24 || i > urls.length)
                                break;

                            // Insert a record into the database
                            mExecutorService.execute(new Thread(){
                                @Override
                                public void run() {
                                    mBreedDao.insertImage(new DogImageEntity(url, breed,subBreed));
                                }
                            });
                        }

                    }catch(Exception e){
                        Log.e(LOG_TAG, "Corrupted data for: " + breed + subBreed);
                        Log.e(LOG_TAG, "Exception: " + e);
                    }

                    result.addSource(loadFromDb(), newData -> setValue(Resource.success(newData)));

                } else {
                    Log.e(LOG_TAG, "API Call getAllBreedImages() Response code: " + response.code());
                    result.addSource(dbSource, newData -> setValue(Resource.error(Resource.ErrorType.NOT_FOUND,newData)));
                }
            }

            @Override
            public void onFailure(Call<BreedImagesResponse> call, Throwable t) {

                if(!isOnline()){
                    Log.d(LOG_TAG,"In the onFailure(): Device is offline");
                    result.addSource(dbSource, newData -> setValue(Resource.error(Resource.ErrorType.NO_CONNECTION,newData)));

                } else {
                    Log.e(LOG_TAG, "API Call getAllBreedImages() Failure: " + t.getMessage());
                    result.addSource(dbSource, newData -> setValue(Resource.error(Resource.ErrorType.SERVER_ERROR,newData)));
                }
            }
        });

    }

    @NonNull
    @Override
    protected LiveData<ResultType> loadFromDb() {
        Log.d(LOG_TAG,"In the loadFromDb(): breed=" + breed + "+subBreed=" + subBreed);

        return (LiveData<ResultType>) mBreedDao.loadBreedImages(breed,subBreed);
    }

    @Override
    protected boolean shouldFetch(@Nullable ResultType data) {
        return (data == null || ((List)data).isEmpty());
    }

    private boolean isOnline() {
        NetworkInfo networkInfo = mConnMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
