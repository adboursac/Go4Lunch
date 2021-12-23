package com.alexdb.go4lunch.data.model.maps;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MapsGeometry {

    @SerializedName("location")
    @Expose
    private final MapsLocation location;

    public MapsGeometry(MapsLocation location) {
        this.location = location;
    }

    public MapsLocation getLocation() {
        return location;
    }
}
