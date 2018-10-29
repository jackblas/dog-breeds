package com.jackblaszkowski.dogbreeds.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jackblaszkowski.dogbreeds.Utils;
import com.jackblaszkowski.dogbreeds.api.BreedImagesResponse;
import com.jackblaszkowski.dogbreeds.api.BreedListResponse;
import com.jackblaszkowski.dogbreeds.api.DogApiClient;
import com.jackblaszkowski.dogbreeds.api.DogApiClientFactory;
import com.jackblaszkowski.dogbreeds.database.AppDatabase;
import com.jackblaszkowski.dogbreeds.database.DogBreedDao;
import com.jackblaszkowski.dogbreeds.database.DogBreedEntity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DogBreedRepository<ResultType> extends Repository<ResultType> {

    private final static String LOG_TAG = "DogBreedRepository";

    private static DogBreedRepository sInstance;
    private ExecutorService mExecutorService;

    private DogBreedDao mBreedDao;

    final private Set<String> mBreedSet = new HashSet<>();
    private boolean refresh;
    private boolean isOnline;


    private DogBreedRepository(Application application) {
        Log.d(LOG_TAG, "DogBreedRepository Created");

        mExecutorService = Executors.newCachedThreadPool();
        AppDatabase database = AppDatabase.getDatabase(application);
        isOnline= Utils.isOnline(application);
        mBreedDao = database.breedDao();
    }

    public static DogBreedRepository getInstance(final Application application) {
        if (sInstance == null) {
            synchronized (DogBreedRepository.class) {
                if (sInstance == null) {
                    sInstance = new DogBreedRepository<List<DogBreedEntity>>(application);
                }
            }
        }
        return sInstance;
    }


    public LiveData<Resource<ResultType>> loadDogBreeds(boolean forceRefresh){
        refresh=forceRefresh;
        return getResource();
    }


    @Override
    protected void fetchFromNetwork(final LiveData<ResultType> dbSource) {
        Log.d(LOG_TAG, "In fetchFromNetwork()");

        DogApiClient client = new DogApiClientFactory().getDogApiClient(DogApiClientFactory.CALL_BREEDS);
        // Get a list of all breeds (including sub-breeds)
        Call<BreedListResponse> call = client.getAllBreeds();

        call.enqueue(new Callback<BreedListResponse>() {
            @Override
            public void onResponse(Call<BreedListResponse> call, Response<BreedListResponse> response) {

                BreedListResponse body = response.body();
                Log.d(LOG_TAG, "In fetchFromNetwork(): " + response.toString());

                if (response.isSuccessful() && (body.getBreeds() != null)) {

                    Set<String> keys = body.getBreeds();
                    mBreedSet.addAll(keys);
                    // We got all breeds
                    // Now, load pictures for each breed:
                    Iterator<String> iterator = mBreedSet.iterator();

                    //int i = 0;
                    //while (iterator.hasNext() && i < 6) {
                    while (iterator.hasNext()) {
                        //i++;
                        String breed = iterator.next();
                        String parts[] = breed.split("-");

                        getRandomImages(parts[0], parts[1]);
                    }
                    // Request a new live data, otherwise we will get last cached value,
                    // which may not be updated with latest results received from network.
                    result.addSource(loadFromDb(), newData -> setValue(Resource.success(newData)));

                } else {
                    Log.e(LOG_TAG, "API Call getAllBreeds() Response code: " + response.code());
                    result.addSource(dbSource, newData -> setValue(Resource.error(Resource.ErrorType.NOT_FOUND,newData)));
                }
            }

            @Override
            public void onFailure(Call<BreedListResponse> call, Throwable t) {

                if(!(isOnline)){
                    Log.d(LOG_TAG,"In the onFailure(): Device is offline");
                    result.addSource(dbSource, newData -> setValue(Resource.error(Resource.ErrorType.NO_CONNECTION,newData)));
                } else {
                    Log.e(LOG_TAG, "API Call getAllBreeds() Failure: " + t.getMessage());
                    result.addSource(dbSource, newData -> setValue(Resource.error(Resource.ErrorType.SERVER_ERROR,newData)));
                }
            }
        });

    }

    @NonNull
    @SuppressWarnings("unchecked")
    @Override
    protected LiveData<ResultType> loadFromDb() {

        return (LiveData<ResultType>) mBreedDao.loadBreeds();
    }

    @Override
    protected boolean shouldFetch(@Nullable ResultType data) {
        return (data == null || ((List)data).isEmpty() || refresh);
    }

    private void getRandomImages(final String breed, final String subBreed) {

        String DUMMY_IMAGE_URL = "https://dog.ceo/api/breed/";

        DogApiClient client = new DogApiClientFactory().getDogApiClient(DogApiClientFactory.CALL_IMAGES);

        Call<BreedImagesResponse> call;

        if (breed.equals(subBreed)) {
            call = client.getBreedImages(breed);
        } else {
            call = client.getSubBreedImages(breed, subBreed);
        }

        call.enqueue(new Callback<BreedImagesResponse>() {
            @Override
            public void onResponse(Call<BreedImagesResponse> call, Response<BreedImagesResponse> response) {
                BreedImagesResponse body = response.body();
                //Log.v(LOG_TAG, "Response 2: " + response.toString());

                if (response.isSuccessful() && (body.getMessage() != null)) {

                    try {
                        String[] urls = body.getMessage();

                        //If less than 3 images, set url to anything: Glide - Path must not be empty.
                        if (urls.length < 3) {
                            switch (urls.length) {
                                case 2:
                                    urls = Arrays.copyOf(urls, urls.length + 1);
                                    urls[2] = DUMMY_IMAGE_URL;
                                    break;
                                case 1:
                                    urls = Arrays.copyOf(urls, urls.length + 2);
                                    urls[2] = DUMMY_IMAGE_URL;
                                    urls[1] = DUMMY_IMAGE_URL;
                                    break;
                                case 0:
                                    urls = Arrays.copyOf(urls, urls.length + 3);
                                    urls[2] = DUMMY_IMAGE_URL;
                                    urls[1] = DUMMY_IMAGE_URL;
                                    urls[0] = DUMMY_IMAGE_URL;
                                    break;

                                default:
                                    break;
                            }
                        }
                        final String[] finalUrls = Arrays.copyOf(urls, urls.length);

                        mExecutorService.execute(new Thread() {
                            @Override
                            public void run() {
                                mBreedDao.insert(new DogBreedEntity(breed, subBreed, finalUrls[0], finalUrls[1], finalUrls[2]));
                            }
                        });

                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Corrupted data for: " + breed + subBreed);
                        Log.e(LOG_TAG, "Exception: " + e);
                    }

                } else {
                    // Log only. Error and network status exposed by refreshData() !
                    Log.e(LOG_TAG, "API Call getBreedImages() Response code: " + response.code());
                }

            }

            @Override
            public void onFailure(Call<BreedImagesResponse> call, Throwable t) {
                // Log only. Error and network status exposed by fetchFromNetwork() !
                Log.e(LOG_TAG, "API Call getBreedImages() Failure: " + t.getMessage());
            }
        });

    }
}
