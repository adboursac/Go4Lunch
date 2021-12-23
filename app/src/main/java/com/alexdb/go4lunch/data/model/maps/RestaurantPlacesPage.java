package com.alexdb.go4lunch.data.model.maps;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RestaurantPlacesPage {

    @SerializedName("status")
    @Expose
    private final String status;

    @SerializedName("results")
    @Expose
    private final List<RestaurantPlace> results;

    @SerializedName("next_page_token")
    @Expose
    private final String next_page_token;

    public RestaurantPlacesPage(String status, List<RestaurantPlace> results, String next_page_token) {
        this.status = status;
        this.results = results;
        this.next_page_token = next_page_token;
    }

    public String getStatus() {
        return status;
    }

    public List<RestaurantPlace> getResults() {
        return results;
    }

    public String getNext_page_token() {
        return next_page_token;
    }
}