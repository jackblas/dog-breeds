package com.jackblaszkowski.dogbreeds;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.util.Log;

import com.jackblaszkowski.dogbreeds.api.BreedImagesResponse;
import com.jackblaszkowski.dogbreeds.api.BreedListResponse;
import com.jackblaszkowski.dogbreeds.api.DogApiClient;
import com.jackblaszkowski.dogbreeds.api.DogApiClientFactory;
import com.jackblaszkowski.dogbreeds.database.AppDatabase;
import com.jackblaszkowski.dogbreeds.database.DogBreedDao;
import com.jackblaszkowski.dogbreeds.database.DogBreedEntity;
import com.jackblaszkowski.dogbreeds.database.DogImageEntity;

import java.util.ArrayList;
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

public class AppDataRepository {

    private final static String LOG_TAG = "AppDataRepository";
    private final static String DUMMY_IMAGE_URL = "https://dog.ceo/api/breed/";

    private static AppDataRepository sInstance;
    private static Context sContext;
    final private Set<String> mBreedSet = new HashSet<>();
    final private List<DogBreedEntity> mDogBreedEntityList = new ArrayList<>();
    private DogBreedDao mBreedDao;
    private ExecutorService mExecutorService;


    private AppDataRepository(Application application) {
        Log.d(LOG_TAG, "AppDataRepository Created");

        AppDatabase database = AppDatabase.getDatabase(application);
        mBreedDao = database.breedDao();

        mExecutorService = Executors.newCachedThreadPool();
    }

    public static AppDataRepository getInstance(final Application application) {
        if (sInstance == null) {
            synchronized (AppDataRepository.class) {
                if (sInstance == null) {
                    sContext = application.getApplicationContext();
                    sInstance = new AppDataRepository(application);
                }
            }
        }
        return sInstance;
    }

    // Returns LiveData for the MainActivityFragment
    public LiveData<List<DogBreedEntity>> getAllBreeds() {

        LiveData<List<DogBreedEntity>> dogsLiveData = mBreedDao.loadMainActivityPictures();
        loadDatabaseIfEmpty();

        return dogsLiveData;
    }

    // Returns LiveData for the MorePhotosFragment
    public LiveData<List<String>> getMoreImages(String breed, String subBreed) {

        LiveData<List<String>> pictureUrls = mBreedDao.loadBreedImages(breed,subBreed);
        loadImagesIfEmpty(breed,subBreed);

        return pictureUrls;
    }


    //Not used
    /*
    public void insertAll (List<DogBreedEntity> dogBreedEntityList) {
        new insertAllAsyncTask(mBreedDao).execute(dogBreedEntityList);
    }

    public void insert (DogBreedEntity dogBreedEntity) {
        new insertAsyncTask(mBreedDao).execute(dogBreedEntity);
    }
    */

    private void loadImagesIfEmpty(final String breed, final String subBreed) {

        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            executor.execute(new Thread() {
                @Override
                public void run() {
                    Log.d(LOG_TAG, "Checking for images for " + breed + "-" + subBreed);
                    DogImageEntity dogBreedEntity = mBreedDao.hasImages(breed,subBreed);

                    if (dogBreedEntity == null) {
                        Log.d(LOG_TAG, "No images: connecting to the server");
                        loadBreedImages(breed,subBreed);
                    } else {
                        Log.d(LOG_TAG, "Images loaded.");
                    }
                }
            });

        } finally {
            executor.shutdown();
        }

    }


    private void loadDatabaseIfEmpty() {

        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            executor.execute(new Thread() {
                @Override
                public void run() {
                    Log.d(LOG_TAG, "Checking if database empty...");
                    DogBreedEntity dogBreedEntity = mBreedDao.hasData();

                    if (dogBreedEntity == null) {
                        Log.d(LOG_TAG, "Database empty: connecting to the server");
                        refreshData();
                    } else {
                        Log.d(LOG_TAG, "Database loaded.");
                    }
                }
            });

        } finally {
            executor.shutdown();
        }

    }


    // Load More Photos
    private void loadBreedImages(final String breed, final String subBreed) {
        Log.d(LOG_TAG, "In loadBreedImages() breed: " + breed + " - subbreed: " + subBreed);
        Utils.setServerStatus(sContext,Utils.STATUS_SERVER_OK);

        DogApiClient client = new DogApiClientFactory().getDogApiClient(DogApiClientFactory.CALL_IMAGES);

        Call<BreedImagesResponse> call;

        if(breed.equals(subBreed)) {
            call = client.listAllBreedImages(breed);

        } else {
            call = client.listAllSubBreedImages(breed,subBreed);
        }

        call.enqueue(new Callback<BreedImagesResponse>() {
            @Override
            public void onResponse(Call<BreedImagesResponse> call, Response<BreedImagesResponse> response) {
                BreedImagesResponse body = response.body();

                // TODO: Handle NullPointerException
                if(response.isSuccessful() && (body.getMessage() != null)) {

                    try {
                        //TODO: Validate url strings here
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
                        //corruptedRecordsCount.incrementAndGet();
                    }

                } else {
                    //TODO: handle server errors if code() is in not the range [200..300)
                    //Log and display server error
                    Log.e("AppDataRepository", "Set Status to: STATUS_SERVER_ERROR");
                    Utils.setServerStatus(sContext,Utils.STATUS_SERVER_ERROR);
                }

            }

            @Override
            public void onFailure(Call<BreedImagesResponse> call, Throwable t) {

                Log.e("AppDataRepository", "API Call listAllBreedImages () Failure: " + t.getMessage());
                //Log and display server error
                Log.e("AppDataRepository", "Set Status to: STATUS_SERVER_ERROR");
                Utils.setServerStatus(sContext,Utils.STATUS_SERVER_ERROR);
            }
        });



    }



    // Call API to refresh
    public void refreshData() {
        Log.d(LOG_TAG, "In refreshData()");
        Utils.setServerStatus(sContext, Utils.STATUS_SERVER_OK);

        DogApiClient client = new DogApiClientFactory().getDogApiClient(DogApiClientFactory.CALL_BREEDS);
        // Get a list of all breeds (including sub-breeds)
        Call<BreedListResponse> call = client.listAllBreeds();

        call.enqueue(new Callback<BreedListResponse>() {
            @Override
            public void onResponse(Call<BreedListResponse> call, Response<BreedListResponse> response) {

                BreedListResponse body = response.body();
                Log.d(LOG_TAG, "Response 1: " + response.toString());

                // TODO: Handle NullPointerException
                if (response.isSuccessful() && (body.getBreeds() != null)) {

                    Set<String> keys = body.getBreeds();
                    mBreedSet.addAll(keys);
                    // We got all breeds
                    // Now, load pictures for each breed:
                    Iterator<String> iterator = mBreedSet.iterator();

                    int i = 0;
                    //while (iterator.hasNext() && i < 7) {
                    while (iterator.hasNext()) {
                        i++;
                        String breed = iterator.next();
                        String parts[] = breed.split("-");

                        getRandomImages(parts[0], parts[1]);

                    }

                    // Wait until we have image urls for all breeds, then shutdown the executor:
                    //TODO: Fix executor shutdown problem
                    mExecutorService.execute(new Thread() {
                        @Override
                        public void run() {
                            Log.d(LOG_TAG, "Thread Running....");
                            Log.d(LOG_TAG, "THREAD breedSet.size()  = " + mBreedSet.size());

                            while((mDogBreedEntityList.size()) < mBreedSet.size()){
                            //while ((!Thread.currentThread().isInterrupted()) && (mDogBreedEntityList.size()) < 7) {
                                // Wait until we have image urls for all breeds

                            }
                            Log.d(LOG_TAG, "Shutting down....");
                            //mExecutorService.shutdown();
                        }
                    });

                } else {
                    //TODO: handle server errors if code() is in not the range [200..300)
                    //Log and display server error
                    Log.d("AppDataRepository", "Set Status to: STATUS_SERVER_ERROR");
                    Utils.setServerStatus(sContext, Utils.STATUS_SERVER_ERROR);
                }

            }

            @Override
            public void onFailure(Call<BreedListResponse> call, Throwable t) {

                Log.e("AppDataRepository", "API Call listAllBreeds() Failure: " + t.getMessage());
                //Log and display server error
                Log.d("AppDataRepository", "Set Status to: STATUS_SERVER_ERROR");
                Utils.setServerStatus(sContext, Utils.STATUS_SERVER_ERROR);
            }
        });


    }


    private void getRandomImages(final String breed, final String subBreed) {
        //Log.v(LOG_TAG, "In getRandomImages()");

        DogApiClient client = new DogApiClientFactory().getDogApiClient(DogApiClientFactory.CALL_IMAGES);

        Call<BreedImagesResponse> call;

        if (breed.equals(subBreed)) {
            call = client.listBreedImages(breed);
        } else {
            call = client.listSubBreedImages(breed, subBreed);
        }

        call.enqueue(new Callback<BreedImagesResponse>() {
            @Override
            public void onResponse(Call<BreedImagesResponse> call, Response<BreedImagesResponse> response) {
                BreedImagesResponse body = response.body();
                Log.v(LOG_TAG, "Response 2: " + response.toString());

                // TODO: Handle NullPointerException
                if (response.isSuccessful() && (body.getMessage() != null)) {

                    try {
                        //TODO: Validate url strings here
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

                        //Accessed within inner class - must be final
                        final String[] finalUrls = Arrays.copyOf(urls, urls.length);

                        DogBreedEntity dogBreedEntity = new DogBreedEntity(breed, subBreed, urls[0], urls[1], urls[2]);
                        mDogBreedEntityList.add(dogBreedEntity);

                        // Insert a record into the database
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
                    //TODO: handle server errors if code() is in not the range [200..300)
                    //Log and display server error
                    Log.e("AppDataRepository", "Set Status to: STATUS_SERVER_ERROR");
                    Utils.setServerStatus(sContext, Utils.STATUS_SERVER_ERROR);
                }

            }

            @Override
            public void onFailure(Call<BreedImagesResponse> call, Throwable t) {

                Log.e("AppDataRepository", "API Call listBreedImages () Failure: " + t.getMessage());
                //Log and display server error
                Log.e("AppDataRepository", "Set Status to: STATUS_SERVER_ERROR");
                Utils.setServerStatus(sContext, Utils.STATUS_SERVER_ERROR);
            }
        });

    }
}
