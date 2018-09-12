package com.jackblaszkowski.dogbreeds.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.Set;

public class BreedListResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private Map<String, String[]> message;

    @Expose(serialize = false, deserialize = false)
    private Set<String> breeds;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, String[]> getMessage() {
        return message;
    }

    public void setMessage(Map<String, String[]> message) {
        this.message = message;
    }

    public Set<String> getBreeds() {
        return breeds;
    }

    public void setBreeds(Set<String> breeds) {
        this.breeds = breeds;
    }
}
