package com.jackblaszkowski.dogbreeds.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DogApiClient {

    String BASE_URL = "https://dog.ceo/api/";

    @GET("breeds/list/all")
    Call<BreedListResponse> getAllBreeds();

    // https://dog.ceo/api/breed/chihuahua/images/random/3
    @GET("breed/{breed}/images/random/3")
    Call<BreedImagesResponse> getBreedImages(@Path("breed") String breed);

    // https://dog.ceo/api/breed/ridgeback/rhodesian/images/random/3
    @GET("breed/{breed}/{subbreed}/images/random/3")
    Call<BreedImagesResponse> getSubBreedImages(@Path("breed") String breed, @Path("subbreed") String subBreed);

    // https://dog.ceo/api/breed/chihuahua/images
    @GET("breed/{breed}/images")
    Call<BreedImagesResponse> getAllBreedImages(@Path("breed") String breed);

    // https://dog.ceo/api/breed/ridgeback/rhodesian/images
    @GET("breed/{breed}/{subbreed}/images")
    Call<BreedImagesResponse> getAllSubBreedImages(@Path("breed") String breed, @Path("subbreed") String subBreed);


}
