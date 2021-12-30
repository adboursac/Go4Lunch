package com.alexdb.go4lunch.data.model.maps;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MapsPlaceDetailsPage {

    @SerializedName("status")
    @Expose
    private final String status;

    @SerializedName("result")
    @Expose
    private final MapsPlaceDetails result;

    public MapsPlaceDetailsPage(String status, MapsPlaceDetails result) {
        this.status = status;
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public MapsPlaceDetails getResult() {
        return result;
    }
}
