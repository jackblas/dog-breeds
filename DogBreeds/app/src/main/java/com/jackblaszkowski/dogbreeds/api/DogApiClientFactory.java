package com.jackblaszkowski.dogbreeds.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DogApiClientFactory {

    public static final String CALL_BREEDS = "breeds";
    public static final String CALL_IMAGES = "images";
    private static DogApiClient breedsClient;
    private static DogApiClient imagesClient;

    static {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(DogApiClient.BASE_URL)
                .addConverterFactory(buildGsonConverter());

        Retrofit retrofit = builder.build();
        retrofit.create(DogApiClient.class);

        breedsClient = retrofit.create(DogApiClient.class);


        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit.Builder builder2 = new Retrofit.Builder()
                .baseUrl(DogApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient);

        Retrofit retrofit2 = builder.build();

        imagesClient = retrofit2.create(DogApiClient.class);


    }

    private static GsonConverterFactory buildGsonConverter() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        // Adding custom deserializers
        gsonBuilder.registerTypeAdapter(BreedListResponse.class, new BreedListDeserializer());
        Gson gson = gsonBuilder.create();

        return GsonConverterFactory.create(gson);
    }

    public DogApiClient getDogApiClient(String callType) {


        if (callType.equals(CALL_BREEDS)) {
            // With custom deserializer
            return breedsClient;

        } else if (callType.equals(CALL_IMAGES)) {
            return imagesClient;

        } else {
            Log.e("DogApiClientFactory", "Unknown type.");
            return null;
        }
    }

}
