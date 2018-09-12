package com.jackblaszkowski.dogbreeds.api;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/*
    BreedListDeserializer - Custom JsonDeserializer
    Returns: List<DogBreedEntity>

    1. Get a list of all breeds -  Call API with https://dog.ceo/api/breeds/list/all
    2. Get a Set of all breeds
    3. Loop the set to find all sub-breeds
        3a. If no sub-breed:
            breed = {breed}-{breed}

        3b. If sub-breed
            breed = {breed}-{subbreed}

 */

public class BreedListDeserializer implements JsonDeserializer<BreedListResponse> {

    @Override
    public BreedListResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        Gson gson = new Gson();
        BreedListResponse breedListResponse = gson.fromJson(json, BreedListResponse.class);

        if (breedListResponse.getStatus().equals("success")) {

            Set<String> breedsSet = new HashSet<>();

            Map<String, String[]> breedsMap = breedListResponse.getMessage();
            Set<String> keys = breedsMap.keySet();
            Iterator<String> iterator = keys.iterator();

            while (iterator.hasNext()) {

                String breed = iterator.next();
                String[] subBreeds = breedsMap.get(breed);

                if (subBreeds.length < 1) {

                    breedsSet.add(new StringBuilder(breed).append("-").append(breed).toString());

                } else {

                    for (String subBreed : subBreeds) {

                        breedsSet.add(new StringBuilder(breed).append("-").append(subBreed).toString());

                    }
                }

            }

            breedListResponse.setBreeds(breedsSet);

        }

        return breedListResponse;
    }
}
