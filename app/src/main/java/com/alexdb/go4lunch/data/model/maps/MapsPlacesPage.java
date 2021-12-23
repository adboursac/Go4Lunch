package com.alexdb.go4lunch.data.model.maps;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MapsPlacesPage {

    @SerializedName("status")
    @Expose
    private final String status;

    @SerializedName("results")
    @Expose
    private final List<MapsPlace> results;

    @SerializedName("next_page_token")
    @Expose
    private final String next_page_token;

    public MapsPlacesPage(String status, List<MapsPlace> results, String next_page_token) {
        this.status = status;
        this.results = results;
        this.next_page_token = next_page_token;
    }

    public String getStatus() {
        return status;
    }

    public List<MapsPlace> getResults() {
        return results;
    }

    public String getNext_page_token() {
        return next_page_token;
    }
}
