package com.alexdb.go4lunch.data.model.maps;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MapsOpeningHours implements Serializable {

    @SerializedName("open_now")
    @Expose
    private final Boolean open_now;

    public MapsOpeningHours(Boolean open_now) {
        this.open_now = open_now;
    }

    public Boolean getOpen_now() {
        return open_now;
    }
}
