package com.alexdb.go4lunch.data.model.maps;

import android.location.Location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RestaurantPlace {

    @SerializedName("place_id")
    @Expose
    private final String placeId;

    @SerializedName("name")
    @Expose
    private final String name;

    @SerializedName("geometry")
    @Expose
    private final MapsGeometry geometry;

    public RestaurantPlace(String placeId, String name, MapsGeometry geometry) {
        this.placeId = placeId;
        this.name = name;
        this.geometry = geometry;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        Location location = new Location("");
        location.setLatitude(geometry.getLocation().getLat());
        location.setLongitude(geometry.getLocation().getLng());
        return location;
    }
}
