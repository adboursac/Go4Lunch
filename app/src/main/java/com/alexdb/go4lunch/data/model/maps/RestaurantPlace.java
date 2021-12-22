package com.alexdb.go4lunch.data.model.maps;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RestaurantPlace {

    @SerializedName("place_id")
    @Expose
    private final String placeId;

    @SerializedName("name")
    @Expose
    private final String name;

    public RestaurantPlace(String placeId, String name) {
        this.placeId = placeId;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
