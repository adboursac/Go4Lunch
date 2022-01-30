package com.alexdb.go4lunch.data.model.maps;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaceOpeningHoursPeriod {

    @SerializedName("close")
    @Expose
    private final PlaceOpeningHoursPeriodDetail close;

    @SerializedName("open")
    @Expose
    private final PlaceOpeningHoursPeriodDetail open;

    public PlaceOpeningHoursPeriod(PlaceOpeningHoursPeriodDetail close, PlaceOpeningHoursPeriodDetail open) {
        this.close = close;
        this.open = open;
    }

    public PlaceOpeningHoursPeriodDetail getClose() {
        return close;
    }

    public PlaceOpeningHoursPeriodDetail getOpen() {
        return open;
    }
}
